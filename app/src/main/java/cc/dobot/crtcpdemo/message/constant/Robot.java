package cc.dobot.crtcpdemo.message.constant;

import androidx.annotation.NonNull;

public interface Robot {
    enum Mode{
        ROBOT_MODE_NO_CONTROLLER,
        ROBOT_MODE_DISCONNECTED,
        ROBOT_MODE_CONFIRM_SAFETY,
        ROBOT_MODE_BOOTING,
        ROBOT_MODE_POWER_OFF,
        ROBOT_MODE_POWER_ON,
        ROBOT_MODE_IDLE,
        ROBOT_MODE_BACKDRIVE,
        ROBOT_MODE_RUNNING,
        ROBOT_MODE_UPDATING_FIRMWARE,
        ROBOT_MODE_ERROR;

        public static Mode getMode(int i){
            switch (i)
            {
                case -1:
                    return ROBOT_MODE_NO_CONTROLLER;
                case 0:
                    return ROBOT_MODE_DISCONNECTED;
                case 1:
                    return ROBOT_MODE_CONFIRM_SAFETY;
                case 2:
                    return ROBOT_MODE_BOOTING;
                case 3:
                    return ROBOT_MODE_POWER_OFF;
                case 4:
                    return ROBOT_MODE_POWER_ON;
                case 5:
                    return ROBOT_MODE_IDLE;
                case 6:
                    return ROBOT_MODE_BACKDRIVE;
                case 7:
                    return ROBOT_MODE_RUNNING;
                case 8:
                    return ROBOT_MODE_UPDATING_FIRMWARE;
                case 9:
                    return ROBOT_MODE_ERROR;
                default:
                    return ROBOT_MODE_NO_CONTROLLER;
            }

        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }
}
