package com.ct.lianlianct;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ct.lianlianct.gameview.GameView;

import java.util.Timer;
import java.util.TimerTask;

/**********************************
 * Author: ct
 * Date: Created in 2019/12/3 10:07
 * Description： TODO
 ***********************************/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GameView gameView;
    ImageButton freshBtn;
    TextView remindTimeTv;
    Button controlBtn;
    int status = 1;//beginning
    final int BEGINNING = 1;
    final int PAUSE = 2;
    final int STOP = 3;
    int time;
    //定时器
    Timer timer;
    //一个TimerTask只能被放置一次，若需要再次放置需要cancel后在放置。
    MyTimerTask timerTask;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {

            remindTimeTv.setText(String.valueOf(time));
            //游戏失败处理
            if (msg.arg1 == -1) {

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                timerTask.cancel();

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle("游戏失败");
                dialog.setMessage("小伙子，再来一次游戏吧");
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "不来了，我认输", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        quit();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "继续挑战", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        refresh();
                    }
                });

                dialog.show();
            }
            if (msg.arg1 == 1) {

                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                timerTask.cancel();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle("胜利");
                dialog.setMessage("好厉害啊");
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "哈哈，休养生息", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        quit();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "不过瘾，再来一次", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        refresh();
                    }
                });
                dialog.show();
            }
            super.handleMessage(msg);
        }
    };

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            Message message = new Message();
            time--;
            //游戏失败
            if (time <= 0) message.arg1 = -1;
            //游戏成功
            if (GameView.VICTORY) {

                message.arg1 = 1;
            }
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.game_view);
        freshBtn = findViewById(R.id.btn_refresh);
        remindTimeTv = findViewById(R.id.tv_time);
        controlBtn = findViewById(R.id.btn_control);
        refresh();

        freshBtn.setOnClickListener(this);
        controlBtn.setOnClickListener(this);
        initTouchEvent();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_refresh:
                refresh();
                break;
            case R.id.btn_control:
                if (status == BEGINNING) {

                    timer.cancel();
                    timer = null;
                    status = PAUSE;
                    controlBtn.setText("继续");

                } else {

                    if (timer == null) {

                        timer = new Timer();
                        timerTask = new MyTimerTask();
                        timer.schedule(timerTask, 1000, 1000);
                    }
                    status = BEGINNING;
                    controlBtn.setText("暂停");
                }
                break;
        }
    }

    //重新开始游戏
    private void refresh() {

        time = 60;
        gameView.restart();
        gameView.invalidate();
        if (timer == null) {

            timer = new Timer();
            timerTask = new MyTimerTask();
            timer.schedule(timerTask, 1000, 1000);
        }
        if (status != BEGINNING) {

            status = BEGINNING;
            controlBtn.setText("暂停");
        }
    }

    //设置touch事件
    public void initTouchEvent() {

        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        gameView.handTouchDownEvent((int) event.getX(), (int) event.getY());
                        //刷新页面
                        gameView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        gameView.handTouchUpEvent();
                        gameView.invalidate();
                        break;
                }

                return true;
            }
        });
    }

    //退出
    public void quit() {

        if (timer != null) {

            timer.cancel();
            timer = null;
        }
        MainActivity.this.finish();
    }
}
