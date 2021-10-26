package cc.dobot.crtcpdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cc.dobot.crtcpdemo.message.constant.Robot;

public class MainActivity extends AppCompatActivity implements MainContract.View, View.OnTouchListener {
    PermissionListener mListener;
    public static final String[] permissionArrays = new String[]{
            Manifest.permission.INTERNET,
    };


    Button connectRobot, powerBTN, enableRobot, clearAlarm, speedRatioBTN, emergencyStop;
    EditText speedRatioEdit;

    Button moveStartBTN, moveStopBTN,getPosBTN;
    EditText moveJEdit[];

    Button pathStartBTN, pathStopBTN;
    EditText pathFileEdit;


    Button setIOBTN, getIOBTN;
    EditText setIOEdit, getIOEdit;
    Spinner setIOSpinner, getIOSpinner;

    TextView robotModeText, speedScalingText, programStateText, DIText, DOText, qActualText, toolVectorActualText;

    TabLayout jogMoveTab;
    Button jogPlusBtn[];
    Button jogMinusBtn[];
    TextView jogText[];

    MainContract.Present present;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRuntimePermission(permissionArrays, new PermissionListener() {
            @Override
            public void onGranted() {
                /*System.out.println("on permission granted");*/
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                new AlertDialog.Builder(MainActivity.this).setCancelable(false)
                        .setTitle(R.string.notice_permission)
                        .setMessage(R.string.permission_text_camera)
                        .setPositiveButton(R.string.exit_app, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        }).show();
            }
        });
        present = new MainPresent(this);
        initView();
    }

    private void initView() {
        connectRobot = findViewById(R.id.button_connect_robot);
        connectRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (present.isConnect())
                    present.disconnectRobot();
                else
                    present.connectRobot();
            }
        });

        enableRobot = findViewById(R.id.button_change_enable);
        enableRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.setRobotEnable(!present.isEnable());

            }
        });

        powerBTN = findViewById(R.id.button_change_power);
        powerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.setRobotPower(true);
            }
        });

        speedRatioEdit = findViewById(R.id.edit_speed_ratio);
        speedRatioBTN = findViewById(R.id.button_speed_ratio);
        speedRatioBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    present.setSpeedRatio(Integer.parseInt(speedRatioEdit.getText().toString()));
                } catch (Exception e) {
                    present.setSpeedRatio(1);
                }

            }
        });

        clearAlarm = findViewById(R.id.button_clear_alarm);
        clearAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.clearAlarm();
            }
        });

        emergencyStop = findViewById(R.id.button_emergency_stop);
        emergencyStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.emergencyStop();
            }
        });

        moveJEdit = new EditText[6];

        moveJEdit[0] = findViewById(R.id.move_j_x_edit);
        moveJEdit[1] = findViewById(R.id.move_j_y_edit);
        moveJEdit[2] = findViewById(R.id.move_j_z_edit);
        moveJEdit[3] = findViewById(R.id.move_j_rx_edit);
        moveJEdit[4] = findViewById(R.id.move_j_ry_edit);
        moveJEdit[5] = findViewById(R.id.move_j_rz_edit);

        moveStartBTN = findViewById(R.id.move_j_move_btn);

        moveStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    present.doMovJ(new double[]{
                            Double.parseDouble(moveJEdit[0].getText().toString()),
                            Double.parseDouble(moveJEdit[1].getText().toString()),
                            Double.parseDouble(moveJEdit[2].getText().toString()),
                            Double.parseDouble(moveJEdit[3].getText().toString()),
                            Double.parseDouble(moveJEdit[4].getText().toString()),
                            Double.parseDouble(moveJEdit[5].getText().toString()),
                    });
                } catch (Exception e) {
                    present.doMovJ(new double[]{
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                    });
                }

            }
        });

        getPosBTN=findViewById(R.id.get_pos_btn);
        getPosBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double toolVectorActual[]=present.getCurrentCoordinate();
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                int i = 0;
                for (double data : toolVectorActual) {
                    i++;
                    data =  (double) (Math.round(data * 10000)) / 10000;
                    toolVectorActual[i - 1] = data;
                    moveJEdit[i-1].setText(nf.format(data));
                }


            }
        });

        moveStopBTN = findViewById(R.id.move_j_stop_btn);
        moveStopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.stopMove();
            }
        });

        pathFileEdit = findViewById(R.id.trace_file_path);
        pathStartBTN = findViewById(R.id.trace_start_path);
        pathStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.startPathTrack(pathFileEdit.getText().toString());
            }
        });
        pathStopBTN = findViewById(R.id.trace_stop_path);
        pathStopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.stopMove();
            }
        });

        setIOBTN = findViewById(R.id.set_io_button);
        setIOBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    present.setIO(Integer.parseInt(setIOEdit.getText().toString()), setIOSpinner.getSelectedItemPosition());
                } catch (Exception e) {
                    present.setIO(1, setIOSpinner.getSelectedItemPosition());
                }
            }
        });

        String[] IOValueRes = new String[]{getString(R.string.low), getString(R.string.high)};
        setIOEdit = findViewById(R.id.set_io_edit);
        setIOSpinner = findViewById(R.id.set_io_spinner);
        setIOSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, IOValueRes));
        setIOSpinner.setSelected(true);
        setIOSpinner.setSelection(0);

        getIOBTN = findViewById(R.id.get_io_button);
        getIOBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getIOSpinner.setSelection(present.getIO(Integer.parseInt(setIOEdit.getText().toString())));
                } catch (Exception e) {
                    getIOSpinner.setSelection(present.getIO(1));
                }
            }
        });
        getIOEdit = findViewById(R.id.get_io_edit);
        getIOSpinner = findViewById(R.id.get_io_spinner);
        getIOSpinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, IOValueRes));
        getIOSpinner.setSelected(true);
        getIOSpinner.setSelection(0);

        robotModeText = findViewById(R.id.robot_mode_text);
        speedScalingText = findViewById(R.id.speed_scaling_text);
        programStateText = findViewById(R.id.program_state_text);
        DIText = findViewById(R.id.digital_input_text);
        DOText = findViewById(R.id.digital_output_text);
        qActualText = findViewById(R.id.q_actual_text);
        toolVectorActualText = findViewById(R.id.tool_vector_actual_text);

        jogText=new TextView[6];
        jogText[0] = findViewById(R.id.jog_j1_text);
        jogText[1] = findViewById(R.id.jog_j2_text);
        jogText[2] = findViewById(R.id.jog_j3_text);
        jogText[3] = findViewById(R.id.jog_j4_text);
        jogText[4] = findViewById(R.id.jog_j5_text);
        jogText[5] = findViewById(R.id.jog_j6_text);

        jogPlusBtn=new Button[6];
        jogPlusBtn[0] = findViewById(R.id.jog_j1_plus_button);
        jogPlusBtn[1] = findViewById(R.id.jog_j2_plus_button);
        jogPlusBtn[2] = findViewById(R.id.jog_j3_plus_button);
        jogPlusBtn[3] = findViewById(R.id.jog_j4_plus_button);
        jogPlusBtn[4] = findViewById(R.id.jog_j5_plus_button);
        jogPlusBtn[5] = findViewById(R.id.jog_j6_plus_button);

        jogMinusBtn=new Button[6];
        jogMinusBtn[0] = findViewById(R.id.jog_j1_minus_button);
        jogMinusBtn[1] = findViewById(R.id.jog_j2_minus_button);
        jogMinusBtn[2] = findViewById(R.id.jog_j3_minus_button);
        jogMinusBtn[3] = findViewById(R.id.jog_j4_minus_button);
        jogMinusBtn[4] = findViewById(R.id.jog_j5_minus_button);
        jogMinusBtn[5] = findViewById(R.id.jog_j6_minus_button);

        jogPlusBtn[0].setOnTouchListener(this);
        jogPlusBtn[1].setOnTouchListener(this);
        jogPlusBtn[2].setOnTouchListener(this);
        jogPlusBtn[3].setOnTouchListener(this);
        jogPlusBtn[4].setOnTouchListener(this);
        jogPlusBtn[5].setOnTouchListener(this);

        jogMinusBtn[0].setOnTouchListener(this);
        jogMinusBtn[1].setOnTouchListener(this);
        jogMinusBtn[2].setOnTouchListener(this);
        jogMinusBtn[3].setOnTouchListener(this);
        jogMinusBtn[4].setOnTouchListener(this);
        jogMinusBtn[5].setOnTouchListener(this);

        jogMoveTab=findViewById(R.id.jog_move_tab);
        jogMoveTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0)
                {
                    jogText[0].setText("J1");
                    jogText[1].setText("J2");
                    jogText[2].setText("J3");
                    jogText[3].setText("J4");
                    jogText[4].setText("J5");
                    jogText[5].setText("J6");
                }else   {
                    jogText[0].setText("x");
                    jogText[1].setText("y");
                    jogText[2].setText("z");
                    jogText[3].setText("rx");
                    jogText[4].setText("ry");
                    jogText[5].setText("rz");
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    int pos = -1;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isPosTouch = true;

        switch (v.getId()) {
            case R.id.jog_j1_plus_button:
                pos = 0;
                break;
            case R.id.jog_j2_plus_button:
                pos = 1;
                break;
            case R.id.jog_j3_plus_button:
                pos = 2;
                break;
            case R.id.jog_j4_plus_button:
                pos = 3;
                break;
            case R.id.jog_j5_plus_button:
                pos = 4;
                break;
            case R.id.jog_j6_plus_button:
                pos = 5;
                break;
            case R.id.jog_j1_minus_button:
                pos = 6;
                break;
            case R.id.jog_j2_minus_button:
                pos = 7;
                break;
            case R.id.jog_j3_minus_button:
                pos = 8;
                break;
            case R.id.jog_j4_minus_button:
                pos = 9;
                break;
            case R.id.jog_j5_minus_button:
                pos = 10;
                break;
            case R.id.jog_j6_minus_button:
                pos = 11;
                break;
            default:
                isPosTouch = false;

        }
        if (event.getAction() == MotionEvent.ACTION_DOWN && isPosTouch) {
            present.setJogMove(jogMoveTab.getSelectedTabPosition()!=0,pos);
        } else if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN && pos != -1 && !isPosTouch) {
            pos = -1;
            present.stopMove();
        } else if (event.getAction() == MotionEvent.ACTION_UP && pos != -1) {
            pos = -1;
            present.stopMove();
        } else if (event.getAction() == MotionEvent.ACTION_POINTER_UP && pos != -1) {
            pos = -1;
            present.stopMove();
        }
        return false;
    };

    public interface PermissionListener {

        void onGranted();

        void onDenied(List<String> deniedPermission);

    }

    public void requestRuntimePermission(String[] permissions, PermissionListener listener) {

        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            mListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED && !permission.equals(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        Toast.makeText(this, R.string.open_permission, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshConnectionState(boolean isConnected) {
        connectRobot.setText(isConnected ? R.string.disconnect_robot : R.string.connect_robot);
    }

    @Override
    public void refreshPowerState(boolean isPowerOn) {
        enableRobot.setText(isPowerOn ? R.string.change_power_off : R.string.change_power_on);
    }

    @Override
    public void refreshEnableState(boolean isEnable) {
        enableRobot.setText(isEnable ? R.string.disable : R.string.enable);
    }

    @Override
    public void refreshSpeedScaling(int speedScaling) {
        speedScalingText.setText("Speed scaling:"+String.valueOf(speedScaling));
    }

    @Override
    public void refreshRobotMode(Robot.Mode mode) {
        robotModeText.setText("Robot mode:"+mode);
    }

    @Override
    public void refreshDI(byte[] DI) {
        String strDI= "DI:"+ String.valueOf(DI[0]) + " " +
                String.valueOf(DI[1]) + " " +
                String.valueOf(DI[2]) + " " +
                String.valueOf(DI[3]) + " " +
                String.valueOf(DI[4]) + " " +
                String.valueOf(DI[5]) + " " +
                String.valueOf(DI[6]) + " " +
                String.valueOf(DI[7]);
        DIText.setText(strDI);
    }

    @Override
    public void refreshDO(byte[] DO) {
        DOText.setText(
                "DO:"+ String.valueOf(DO[0]) + " " +
                        String.valueOf(DO[1]) + " " +
                        String.valueOf(DO[2]) + " " +
                        String.valueOf(DO[3]) + " " +
                        String.valueOf(DO[4]) + " " +
                        String.valueOf(DO[5]) + " " +
                        String.valueOf(DO[6]) + " " +
                        String.valueOf(DO[7]));
    }

    @Override
    public void refreshQActual(double[] getqActual) {

        int i = 0;
        for (double data : getqActual) {
            i++;
            data =  (double)(Math.round(data * 10000)) / 10000;
            getqActual[i - 1] = data;
        }
            //System.out.println("j data "+i+" 1:"+data);
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);

        qActualText.setText(
                "j1:"+nf.format(getqActual[0])+
                        " j2:"+nf.format(getqActual[1])+
                        " j3:"+nf.format(getqActual[2])+
                        "\nj4:"+nf.format(getqActual[3])+
                        " j5:"+nf.format(getqActual[4])+
                        " j6:"+nf.format(getqActual[5]));
    }

    @Override
    public void refreshToolVectorActual(double[] toolVectorActual) {
        int i = 0;
        for (double data : toolVectorActual) {
            i++;
            data =  (double) (Math.round(data * 10000)) / 10000;
            toolVectorActual[i - 1] = data;
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        toolVectorActualText.setText(
                "x:"+toolVectorActual[0]+
                        " y:"+nf.format(toolVectorActual[1])+
                        " z:"+nf.format(toolVectorActual[2])+
                        "\nrx:"+nf.format(toolVectorActual[3])+
                        " ry:"+nf.format(toolVectorActual[4])+
                        " rz:"+nf.format(toolVectorActual[5]));
    }

    @Override
    public void refreshProgramState(int programState) {
        programStateText.setText("Program state:"+programState);
    }
}