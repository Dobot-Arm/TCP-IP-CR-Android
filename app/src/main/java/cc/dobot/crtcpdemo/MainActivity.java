package cc.dobot.crtcpdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import java.util.LinkedList;
import java.util.List;

import cc.dobot.crtcpdemo.adapter.TextItemAdapter;
import cc.dobot.crtcpdemo.data.AlarmData;
import cc.dobot.crtcpdemo.message.constant.Robot;

public class MainActivity extends AppCompatActivity implements MainContract.View, View.OnTouchListener {
    PermissionListener mListener;
    public static final String[] permissionArrays = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    Handler handler=new Handler();

    EditText ipEdit, dashPortEdit, movePortEdit, feedBackPortEdit;
    String currentIP = "192.168.1.6";
    int dashPort = 29999;
    int movePort = 30003;
    int feedBackPort = 30004;
    Button connectRobot, resetRobot, enableRobot, clearAlarm, speedRatioBTN/*,emergencyStop*/;
    EditText speedRatioEdit;

    Button moveStartBTN, moveLStartBTN, moveStopBTN, getPosBTN;
    Button jointMovJStartBTN, jointMovJStopBTN, getJointPosBTN;
    EditText moveJEdit[], jointJEdit[];

/*    Button pathStartBTN, pathStopBTN;
    EditText pathFileEdit;*/


    Button setIOBTN/*, getIOBTN*/;
    EditText setIOEdit/*, getIOEdit*/;
    Spinner setIOSpinner/*, getIOSpinner*/;

    TextView robotModeText, speedScalingText, programStateText, DIText, DOText, qActualText, toolVectorActualText;

    // TabLayout jogMoveTab;
    Button jogPlusBtn[];
    Button jogMinusBtn[];
    TextView jogText[];

    Button coordPlusBtn[];
    Button coordMinusBtn[];
    TextView coordText[];

    RecyclerView errorListRV;
    TextItemAdapter errorListAdapter;
    List<String> errorList;

    RecyclerView logListRV;
    TextItemAdapter logListAdapter;
    List<String> logList;

    MainContract.Present present;

    private void changeViewStats(boolean b) {
       /* ipEdit.setEnabled(b);
        dashPortEdit.setEnabled(b);
        movePortEdit.setEnabled(b);
        feedBackPortEdit.setEnabled(b);*/
        enableRobot.setEnabled(b);
        resetRobot.setEnabled(b);
        clearAlarm.setEnabled(b);
        speedRatioBTN.setEnabled(b);
        speedRatioEdit.setEnabled(b);
        moveStartBTN.setEnabled(b); moveLStartBTN.setEnabled(b); moveStopBTN.setEnabled(b); getPosBTN.setEnabled(b);
        jointMovJStartBTN.setEnabled(b); jointMovJStopBTN.setEnabled(b); getJointPosBTN.setEnabled(b);
        for (EditText edit:moveJEdit)
        {
            edit.setEnabled(b);
        }
        for (EditText edit:jointJEdit)
        {
            edit.setEnabled(b);
        }

        setIOBTN.setEnabled(b);
        setIOEdit.setEnabled(b);
        setIOSpinner.setEnabled(b);

        // TabLayout jogMoveTab;
        for (Button btn:jogMinusBtn)
            btn.setEnabled(b);
        for (Button btn:jogPlusBtn)
            btn.setEnabled(b);
        for (Button btn:coordMinusBtn)
            btn.setEnabled(b);
        for (Button btn:coordPlusBtn)
            btn.setEnabled(b);

        findViewById(R.id.button_clear_error_info_list).setEnabled(b);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
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
        changeViewStats(false);
    }

    private void initView() {
        ipEdit = findViewById(R.id.ip_address_edit);
        ipEdit.setText(currentIP);

        dashPortEdit = findViewById(R.id.dashboard_port_edit);
        dashPortEdit.setText(String.valueOf(dashPort));

        movePortEdit = findViewById(R.id.move_port_edit);
        movePortEdit.setText(String.valueOf(movePort));

        feedBackPortEdit = findViewById(R.id.feedback_port_edit);
        feedBackPortEdit.setText(String.valueOf(feedBackPort));

        connectRobot = findViewById(R.id.button_connect_robot);
        connectRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (present.isConnected()){
                    present.disconnectRobot();
                    changeViewStats(false);
                }
                else {
                    currentIP=ipEdit.getText().toString();
                    dashPort=Integer.parseInt(dashPortEdit.getText().toString());
                    movePort= Integer.parseInt(movePortEdit.getText().toString());
                    feedBackPort= Integer.parseInt(feedBackPortEdit.getText().toString());
                    present.connectRobot(currentIP, dashPort, movePort, feedBackPort);
                    changeViewStats(true);
                }
            }
        });

        enableRobot = findViewById(R.id.button_change_enable);
        enableRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.setRobotEnable(!present.isEnable());

            }
        });

        resetRobot=findViewById(R.id.button_reset_robot);
        resetRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                present.resetRobot();
            }
        });
     /*   powerBTN = findViewById(R.id.button_change_power);
        powerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.setRobotPower(true);
            }
        });*/

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

      /*  emergencyStop = findViewById(R.id.button_emergency_stop);
        emergencyStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.emergencyStop();
            }
        });*/

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

        moveLStartBTN = findViewById(R.id.move_l_move_btn);
        moveLStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    present.doMovL(new double[]{
                            Double.parseDouble(moveJEdit[0].getText().toString()),
                            Double.parseDouble(moveJEdit[1].getText().toString()),
                            Double.parseDouble(moveJEdit[2].getText().toString()),
                            Double.parseDouble(moveJEdit[3].getText().toString()),
                            Double.parseDouble(moveJEdit[4].getText().toString()),
                            Double.parseDouble(moveJEdit[5].getText().toString()),
                    });
                } catch (Exception e) {
                    present.doMovL(new double[]{
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

        getPosBTN = findViewById(R.id.get_pos_btn);
        getPosBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double toolVectorActual[] = present.getCurrentCoordinate();
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                int i = 0;
                for (double data : toolVectorActual) {
                    i++;
                    data = (double) (Math.round(data * 10000)) / 10000;
                    toolVectorActual[i - 1] = data;
                    moveJEdit[i - 1].setText(nf.format(data));
                }


            }
        });

        moveStopBTN = findViewById(R.id.move_j_stop_btn);
        moveStopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.stopScript();
            }
        });

        jointJEdit = new EditText[6];

        jointJEdit[0] = findViewById(R.id.joint_move_j_j1_edit);
        jointJEdit[1] = findViewById(R.id.joint_move_j_j2_edit);
        jointJEdit[2] = findViewById(R.id.joint_move_j_j3_edit);
        jointJEdit[3] = findViewById(R.id.joint_move_j_j4_edit);
        jointJEdit[4] = findViewById(R.id.joint_move_j_j5_edit);
        jointJEdit[5] = findViewById(R.id.joint_move_j_j6_edit);

        jointMovJStartBTN = findViewById(R.id.joint_move_j_move_btn);

        jointMovJStartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    present.doJointMovJ(new double[]{
                            Double.parseDouble(jointJEdit[0].getText().toString()),
                            Double.parseDouble(jointJEdit[1].getText().toString()),
                            Double.parseDouble(jointJEdit[2].getText().toString()),
                            Double.parseDouble(jointJEdit[3].getText().toString()),
                            Double.parseDouble(jointJEdit[4].getText().toString()),
                            Double.parseDouble(jointJEdit[5].getText().toString()),
                    });
                } catch (Exception e) {
                    present.doJointMovJ(new double[]{
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

        getJointPosBTN = findViewById(R.id.get_joint_btn);
        getJointPosBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double qActual[] = present.getCurrentJoint();
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(false);
                int i = 0;
                for (double data : qActual) {
                    i++;
                    data = (double) (Math.round(data * 10000)) / 10000;
                    qActual[i - 1] = data;
                    jointJEdit[i - 1].setText(nf.format(data));
                }


            }
        });

        jointMovJStopBTN = findViewById(R.id.joint_move_j_stop_btn);
        jointMovJStopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                present.stopScript();
            }
        });


       /* pathFileEdit = findViewById(R.id.trace_file_path);
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
                present.stopScript();
            }
        });*/

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

        /*getIOBTN = findViewById(R.id.get_io_button);
        getIOBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getIOSpinner.setSelection(present.getIO(Integer.parseInt(getIOEdit.getText().toString())));
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
*/
        robotModeText = findViewById(R.id.robot_mode_text);
        speedScalingText = findViewById(R.id.speed_scaling_text);
       // programStateText = findViewById(R.id.program_state_text);
        DIText = findViewById(R.id.digital_input_text);
        DOText = findViewById(R.id.digital_output_text);
      /*  qActualText = findViewById(R.id.q_actual_text);
        toolVectorActualText = findViewById(R.id.tool_vector_actual_text);*/

        jogText = new TextView[6];
        jogText[0] = findViewById(R.id.jog_j1_text);
        jogText[1] = findViewById(R.id.jog_j2_text);
        jogText[2] = findViewById(R.id.jog_j3_text);
        jogText[3] = findViewById(R.id.jog_j4_text);
        jogText[4] = findViewById(R.id.jog_j5_text);
        jogText[5] = findViewById(R.id.jog_j6_text);

        jogPlusBtn = new Button[6];
        jogPlusBtn[0] = findViewById(R.id.jog_j1_plus_button);
        jogPlusBtn[1] = findViewById(R.id.jog_j2_plus_button);
        jogPlusBtn[2] = findViewById(R.id.jog_j3_plus_button);
        jogPlusBtn[3] = findViewById(R.id.jog_j4_plus_button);
        jogPlusBtn[4] = findViewById(R.id.jog_j5_plus_button);
        jogPlusBtn[5] = findViewById(R.id.jog_j6_plus_button);

        jogMinusBtn = new Button[6];
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


        coordText = new TextView[6];
        coordText[0] = findViewById(R.id.coordinate_x_text);
        coordText[1] = findViewById(R.id.coordinate_y_text);
        coordText[2] = findViewById(R.id.coordinate_z_text);
        coordText[3] = findViewById(R.id.coordinate_rx_text);
        coordText[4] = findViewById(R.id.coordinate_ry_text);
        coordText[5] = findViewById(R.id.coordinate_rz_text);

        coordPlusBtn = new Button[6];
        coordPlusBtn[0] = findViewById(R.id.coordinate_x_plus_button);
        coordPlusBtn[1] = findViewById(R.id.coordinate_y_plus_button);
        coordPlusBtn[2] = findViewById(R.id.coordinate_z_plus_button);
        coordPlusBtn[3] = findViewById(R.id.coordinate_rx_plus_button);
        coordPlusBtn[4] = findViewById(R.id.coordinate_ry_plus_button);
        coordPlusBtn[5] = findViewById(R.id.coordinate_rz_plus_button);

        coordMinusBtn = new Button[6];
        coordMinusBtn[0] = findViewById(R.id.coordinate_x_minus_button);
        coordMinusBtn[1] = findViewById(R.id.coordinate_y_minus_button);
        coordMinusBtn[2] = findViewById(R.id.coordinate_z_minus_button);
        coordMinusBtn[3] = findViewById(R.id.coordinate_rx_minus_button);
        coordMinusBtn[4] = findViewById(R.id.coordinate_ry_minus_button);
        coordMinusBtn[5] = findViewById(R.id.coordinate_rz_minus_button);

        coordPlusBtn[0].setOnTouchListener(this);
        coordPlusBtn[1].setOnTouchListener(this);
        coordPlusBtn[2].setOnTouchListener(this);
        coordPlusBtn[3].setOnTouchListener(this);
        coordPlusBtn[4].setOnTouchListener(this);
        coordPlusBtn[5].setOnTouchListener(this);

        coordMinusBtn[0].setOnTouchListener(this);
        coordMinusBtn[1].setOnTouchListener(this);
        coordMinusBtn[2].setOnTouchListener(this);
        coordMinusBtn[3].setOnTouchListener(this);
        coordMinusBtn[4].setOnTouchListener(this);
        coordMinusBtn[5].setOnTouchListener(this);



     /*   jogMoveTab=findViewById(R.id.jog_move_tab);
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
        });*/
        errorListRV=findViewById(R.id.error_info_list);
        errorListRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        errorList=new LinkedList<>();
        errorListAdapter=new TextItemAdapter(errorList);
        errorListRV.setAdapter(errorListAdapter);

        findViewById(R.id.button_clear_error_info_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorList.clear();
                errorListAdapter.notifyDataSetChanged();
            }
        });

        logListRV=findViewById(R.id.log_list);
        logListRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        logList=new LinkedList<>();
        logListAdapter=new TextItemAdapter(logList);
        logListRV.setAdapter(logListAdapter);
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
            case R.id.coordinate_x_plus_button:
                pos = 12;
                break;
            case R.id.coordinate_y_plus_button:
                pos = 13;
                break;
            case R.id.coordinate_z_plus_button:
                pos = 14;
                break;
            case R.id.coordinate_rx_plus_button:
                pos = 15;
                break;
            case R.id.coordinate_ry_plus_button:
                pos = 16;
                break;
            case R.id.coordinate_rz_plus_button:
                pos = 17;
                break;
            case R.id.coordinate_x_minus_button:
                pos = 18;
                break;
            case R.id.coordinate_y_minus_button:
                pos = 19;
                break;
            case R.id.coordinate_z_minus_button:
                pos = 20;
                break;
            case R.id.coordinate_rx_minus_button:
                pos = 21;
                break;
            case R.id.coordinate_ry_minus_button:
                pos = 22;
                break;
            case R.id.coordinate_rz_minus_button:
                pos = 23;
                break;
            default:
                isPosTouch = false;

        }
        if (event.getAction() == MotionEvent.ACTION_DOWN && isPosTouch) {
            if (pos<12)
                present.setJogMove(false, pos);
            else
                present.setJogMove(true, pos-12);
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
    }

    ;

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
        speedScalingText.setText(getString(R.string.speed_ratio_) + String.valueOf(speedScaling));
    }

    @Override
    public void refreshRobotMode(Robot.Mode mode) {
        robotModeText.setText(getString(R.string.robot_mode_text) + mode);
    }

    @Override
    public void refreshDI(byte[] DI) {
        String strDI =getString(R.string.digital_input_text) + String.valueOf(DI[0]) + " " +
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
                getString(R.string.digital_outputs_text) + String.valueOf(DO[0]) + " " +
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
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        for (double data : getqActual) {
            i++;
            data = (double) (Math.round(data * 10000)) / 10000;
            getqActual[i - 1] = data;
            jogText[i-1].setText("J"+i+":"+nf.format(getqActual[i-1]));
        }
        //System.out.println("j data "+i+" 1:"+data);


        /*qActualText.setText(
                "j1:" + nf.format(getqActual[0]) +
                        " j2:" + nf.format(getqActual[1]) +
                        " j3:" + nf.format(getqActual[2]) +
                        "\nj4:" + nf.format(getqActual[3]) +
                        " j5:" + nf.format(getqActual[4]) +
                        " j6:" + nf.format(getqActual[5]));*/
    }

    @Override
    public void refreshToolVectorActual(double[] toolVectorActual) {
        int i = 0;
        for (double data : toolVectorActual) {
            i++;
            data = (double) (Math.round(data * 10000)) / 10000;
            toolVectorActual[i - 1] = data;
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        coordText[0].setText(  "X:" + toolVectorActual[0]);
        coordText[1].setText(  "Y:" + toolVectorActual[1]);
        coordText[2].setText(  "Z:" + toolVectorActual[2]);
        coordText[3].setText(  "RX:" + toolVectorActual[3]);
        coordText[4].setText(  "RY:" + toolVectorActual[4]);
        coordText[5].setText(  "RZ:" + toolVectorActual[5]);
    }

    @Override
    public void refreshProgramState(int programState) {
     //   programStateText.setText("Program state:" + programState);
    }

    @Override
    public void refreshErrorList(String errorInfo) {
        errorList.add(errorInfo);
        errorListAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshLogList(final boolean isSend, final String log) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                logList.add((isSend?getString(R.string.send_msg):getString(R.string.receive_msg))+log);
                logListAdapter.notifyDataSetChanged();
            }
        });
    };

    @Override
    public void refreshAlarmList(List<AlarmData> dataList) {
        errorList.clear();
        for (AlarmData data:dataList)
            errorList.add(data.toString());
        errorListAdapter.notifyDataSetChanged();
    }
}