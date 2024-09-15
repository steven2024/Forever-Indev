package net.minecraft.client.player;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public final class MovementInputFromOptions extends MovementInput {
    private boolean[] keyboardMovementStates = new boolean[6]; // Separate for keyboard: Forward, back, left, right, jump, sprint
    private boolean[] gamepadMovementStates = new boolean[6]; // Separate for gamepad
    private GameSettings gameSettings;
    private long lastForwardPressTime = 0L;
    private final long doubleTapThreshold = 200L;
    public static boolean isSprinting = false;
    private boolean forwardWasReleased = false;

    public static float maxStamina = 10.0f;
    public static float staminaRemaining = maxStamina;
    public static float speedMultiplier;
    private float staminaRate = 0.1f;
    private boolean isExhausted = false;
    private boolean canSprint = true;
    private long lastStaminaUpdateTime = 0L;
    private Minecraft mc;

    private boolean hasInitializedStamina = false;
    private int lastHealth = -1;

    private Controller gamepad;
    private static final float DEADZONE_THRESHOLD = 0.05f;

    private static final int FORWARD_INDEX = 0;
    private static final int BACK_INDEX = 1;
    private static final int LEFT_INDEX = 2;
    private static final int RIGHT_INDEX = 3;
    private static final int JUMP_INDEX = 4;
    private static final int SPRINT_INDEX = 5;

    public MovementInputFromOptions(GameSettings gameSettings, Minecraft mc) {
        this.gameSettings = gameSettings;
        this.mc = mc;
        initializeStamina();
        initializeController();
    }

    private void initializeStamina() {
        if (!hasInitializedStamina) {
            staminaRemaining = maxStamina;
            lastHealth = this.mc.thePlayer.health;
            hasInitializedStamina = true;
        }
    }

    private void initializeController() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (Controller controller : controllers) {
            if (controller.getType() == Controller.Type.GAMEPAD) {
                gamepad = controller;
                break;
            }
        }
    }

    @Override
    public final void checkKeyForMovementInput(int keyCode, boolean isPressed) {
        int keyIndex = -1;

        if (keyCode == this.gameSettings.keyBindForward.keyCode) {
            keyIndex = FORWARD_INDEX;
        } else if (keyCode == this.gameSettings.keyBindBack.keyCode) {
            keyIndex = BACK_INDEX;
        } else if (keyCode == this.gameSettings.keyBindLeft.keyCode) {
            keyIndex = LEFT_INDEX;
        } else if (keyCode == this.gameSettings.keyBindRight.keyCode) {
            keyIndex = RIGHT_INDEX;
        } else if (keyCode == this.gameSettings.keyBindJump.keyCode) {
            keyIndex = JUMP_INDEX;
        } else if (keyCode == this.gameSettings.keyBindSprint.keyCode) {
            keyIndex = SPRINT_INDEX;
        }

        if (keyIndex >= 0) {
            this.keyboardMovementStates[keyIndex] = isPressed;

            if (keyIndex == SPRINT_INDEX && isPressed && canSprint) {
                startSprinting();
            }

            if (keyIndex == FORWARD_INDEX && isPressed && canSprint) {
                long currentTime = System.currentTimeMillis();
                if (forwardWasReleased && (currentTime - lastForwardPressTime < this.doubleTapThreshold)) {
                    startSprinting();
                    forwardWasReleased = false;
                }
                lastForwardPressTime = currentTime;
            } else if (keyIndex == FORWARD_INDEX && !isPressed) {
                forwardWasReleased = true;
            }

            if ((keyIndex == FORWARD_INDEX && !isPressed) || (keyIndex == SPRINT_INDEX && !isPressed)) {
                stopSprinting();
            }
        }
    }

    private void pollControllerInput() {
        if (gamepad != null) {
            gamepad.poll();

            for (Component component : gamepad.getComponents()) {
                float pollData = component.getPollData();

                if (Math.abs(pollData) < DEADZONE_THRESHOLD) {
                    pollData = 0;
                }

                if (component.getName().equalsIgnoreCase("X Axis") || component.getName().equalsIgnoreCase("X")) {
                    if (pollData < -DEADZONE_THRESHOLD) {
                        this.gamepadMovementStates[LEFT_INDEX] = true;
                        this.gamepadMovementStates[RIGHT_INDEX] = false;
                    } else if (pollData > DEADZONE_THRESHOLD) {
                        this.gamepadMovementStates[RIGHT_INDEX] = true;
                        this.gamepadMovementStates[LEFT_INDEX] = false;
                    } else {
                        this.gamepadMovementStates[LEFT_INDEX] = false;
                        this.gamepadMovementStates[RIGHT_INDEX] = false;
                    }
                }

                if (component.getName().equalsIgnoreCase("Y Axis") || component.getName().equalsIgnoreCase("Y")) {
                    if (pollData < -DEADZONE_THRESHOLD) {
                        this.gamepadMovementStates[FORWARD_INDEX] = true;
                        this.gamepadMovementStates[BACK_INDEX] = false;
                    } else if (pollData > DEADZONE_THRESHOLD) {
                        this.gamepadMovementStates[BACK_INDEX] = true;
                        this.gamepadMovementStates[FORWARD_INDEX] = false;
                    } else {
                        this.gamepadMovementStates[FORWARD_INDEX] = false;
                        this.gamepadMovementStates[BACK_INDEX] = false;
                    }
                }

                if (component.getName().equals("Button 0")) {
                    this.gamepadMovementStates[JUMP_INDEX] = pollData > 0.5f;
                }

                if (component.getName().equals("Button 8")) {
                    this.gamepadMovementStates[SPRINT_INDEX] = pollData > 0.5f;
                    if (this.gamepadMovementStates[SPRINT_INDEX] && canSprint) {
                        startSprinting();
                    } else {
                        stopSprinting();
                    }
                }
            }
        }
    }

    private void startSprinting() {
        int playerHealth = this.mc.thePlayer.health;
        if (staminaRemaining > 0 && canSprint && playerHealth > 6) {
            isSprinting = true;
        }
    }

    private void stopSprinting() {
        isSprinting = false;
    }

    private void updateMaxStamina() {
        int playerHealth = this.mc.thePlayer.health;

        if (playerHealth < lastHealth) {
            int lostHealth = lastHealth - playerHealth;
            float timeLost = lostHealth * 1.0f;
            staminaRemaining = Math.max(0, staminaRemaining - timeLost);
        }

        maxStamina = playerHealth * 1.0f;
        staminaRemaining = Math.min(staminaRemaining, maxStamina);
        lastHealth = playerHealth;

        if (this.mc.thePlayer.isCreativeMode) {
            canSprint = true;
        }

        if (playerHealth <= 6) {
            canSprint = false;
            stopSprinting();
        } else if (!isExhausted) {
            canSprint = true;
        }
    }

    @Override
    public final void updatePlayerMoveState() {
        pollControllerInput();

        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        long currentTime = System.currentTimeMillis();
        boolean isMoving = (this.keyboardMovementStates[FORWARD_INDEX] || this.keyboardMovementStates[BACK_INDEX] || this.keyboardMovementStates[LEFT_INDEX] || this.keyboardMovementStates[RIGHT_INDEX])
                        || (this.gamepadMovementStates[FORWARD_INDEX] || this.gamepadMovementStates[BACK_INDEX] || this.gamepadMovementStates[LEFT_INDEX] || this.gamepadMovementStates[RIGHT_INDEX]);

        if (!hasInitializedStamina) {
            initializeStamina();
        }

        updateMaxStamina();

        if (isSprinting && isMoving && currentTime - lastStaminaUpdateTime >= 50) {
            staminaRemaining -= staminaRate;
            if (staminaRemaining <= 0) {
                staminaRemaining = 0;
                stopSprinting();
                isExhausted = true;
                canSprint = false;
            }
            lastStaminaUpdateTime = currentTime;
        }

        if ((!isSprinting || !isMoving) && currentTime - lastStaminaUpdateTime >= 50) {
            if (isExhausted) {
                staminaRemaining += staminaRate * 0.5f;
                if (staminaRemaining >= maxStamina) {
                    staminaRemaining = maxStamina;
                    isExhausted = false;
                    canSprint = true;
                }
            } else if (staminaRemaining < maxStamina) {
                staminaRemaining = Math.min(staminaRemaining + staminaRate, maxStamina);
            }
            lastStaminaUpdateTime = currentTime;
        }

        // Combine movement states from both keyboard and gamepad
        if (this.keyboardMovementStates[FORWARD_INDEX] || this.gamepadMovementStates[FORWARD_INDEX]) {
            this.moveForward = 1.0F;
        }

        if (this.keyboardMovementStates[BACK_INDEX] || this.gamepadMovementStates[BACK_INDEX]) {
            this.moveForward = -1.0F;
        }

        if (this.keyboardMovementStates[LEFT_INDEX] || this.gamepadMovementStates[LEFT_INDEX]) {
            this.moveStrafe = 1.0F;
        }

        if (this.keyboardMovementStates[RIGHT_INDEX] || this.gamepadMovementStates[RIGHT_INDEX]) {
            this.moveStrafe = -1.0F;
        }

        this.jump = this.keyboardMovementStates[JUMP_INDEX] || this.gamepadMovementStates[JUMP_INDEX];
    }
}
