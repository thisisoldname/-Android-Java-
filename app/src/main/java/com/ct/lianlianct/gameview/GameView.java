package com.ct.lianlianct.gameview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ct.lianlianct.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**********************************
 * Author: ct
 * Date: Created in 2019/12/3 10:01
 * Description： 显示游戏自定义view
 * Draw:通过gameMap绘制图形，并封装了大量操作方法
 ***********************************/
public class GameView extends View {

    Context context;
    //游戏格子
    GameItem gameMap[][];
    //画笔
    Paint painter;
    //连接线存储
    List<Point> linkList = new ArrayList<>();
    //前一次点击图片的序号
    int preImage = -1;
    //前一次点击图片位置
    Point prePostion = new Point(-1, -1);
    public static boolean VICTORY = false;
    int n = 8;
    int itemWidth = 1000 / n;

    //创建一个n*n的连连看格子
    public GameView(Context context, AttributeSet attr) {

        super(context, attr);
        this.context = context;
        //初始化画笔
        painter = new Paint();
        painter.setColor(Color.RED);
        painter.setStrokeWidth(5);

        //初始化游戏格子
        gameMap = new GameItem[n][n];
        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                gameMap[i][j] = new GameItem();
            }
        }
        //创建gameMap
        createGameMap();
    }

    //绘制方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap image;
        int id;
        //绘制
        VICTORY = true;
        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                if (!gameMap[i][j].getIsHide()) {

                    VICTORY = false;
                    id = gameMap[i][j].getId();
                    image = BitmapFactory.decodeResource(context.getResources(), getImageByid(id));
                    canvas.drawBitmap(image, null, gameMap[i][j].getLocationRect(), null);
                }
            }
        }

        //绘制连线
        if (linkList.isEmpty()) {

            return;
        } else {

            for (int i = 0; i < linkList.size() - 1; i++) {

                int x1 = linkList.get(i).x * itemWidth + itemWidth / 2;
                int y1 = linkList.get(i).y * itemWidth + itemWidth / 2;
                int x2 = linkList.get(i + 1).x * itemWidth + itemWidth / 2;
                int y2 = linkList.get(i + 1).y * itemWidth + itemWidth / 2;
                canvas.drawLine(x1, y1, x2, y2, painter);
            }
        }

    }

    //创造随机的二维偶矩阵,n必须为偶数
    public void createGameMap() {

        if (n % 2 != 0) return;

        //存储游戏的格子的图片的序号
        List<Integer> gameList = new ArrayList<>();

        Rect rect;
        Random random = new Random();
        //随机矩阵
        for (int i = 0; i < (n - 2) * (n - 2) / 2; i++) {

            gameList.add(random.nextInt(20) + 1);
        }

        for (int i = 0; i < (n - 2) * (n - 2) / 2; i++) {

            int m = (n - 2) * (n - 2) / 2;
            gameList.add(gameList.get((i + 5) % m));
        }

        int m = 0;
        //将随机的item装到gameMap
        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                if (i == 0 || i == n - 1 || j == 0 || j == n - 1) {

                    gameMap[i][j].setIsHide(true);
                    gameMap[i][j].setId(0);
                } else {
                    int t = gameList.get(m);
                    gameMap[i][j].setIsHide(false);
                    gameMap[i][j].setId(t);
                    m++;
                }
                //保证图片直接有空隙
                rect = new Rect(j * itemWidth + 10, i * itemWidth + 10, (j + 1) * itemWidth - 10, (i + 1) * itemWidth - 10);
                gameMap[i][j].setLocationRect(rect);

            }
        }
        return;
    }

    //通过id获得图片的资源地址，方法有点垃圾
    private int getImageByid(int id) {

        switch (id) {
            case 1:
                return R.drawable.cartoon_1;
            case 2:
                return R.drawable.cartoon_2;
            case 3:
                return R.drawable.cartoon_3;
            case 4:
                return R.drawable.cartoon_4;
            case 5:
                return R.drawable.cartoon_5;
            case 6:
                return R.drawable.cartoon_6;
            case 7:
                return R.drawable.cartoon_7;
            case 8:
                return R.drawable.cartoon_8;
            case 9:
                return R.drawable.cartoon_9;
            case 10:
                return R.drawable.cartoon_10;
            case 11:
                return R.drawable.cartoon_11;
            case 12:
                return R.drawable.cartoon_12;
            case 13:
                return R.drawable.cartoon_13;
            case 14:
                return R.drawable.cartoon_14;
            case 15:
                return R.drawable.cartoon_15;
            case 16:
                return R.drawable.cartoon_16;
            case 17:
                return R.drawable.cartoon_17;
            case 18:
                return R.drawable.cartoon_18;
            case 19:
                return R.drawable.cartoon_19;
            case 20:
                return R.drawable.cartoon_20;

        }
        return -1;
    }

    //处理触碰按下事件，是否连接前图片和当前点击图片,即移除两张图片
    public void handTouchDownEvent(int j, int i) {

        i = i / itemWidth;
        j = j / itemWidth;

        //如果点击的是同一位置图片则不做操作
        if (prePostion.x == i && prePostion.y == j) return;
        //如果第一次点击
        if (preImage == -1) {

            //点击图片放大
            gameMap[i][j].setLocationRect(new Rect(j * itemWidth + 5, i * itemWidth + 5, (j + 1) * itemWidth - 5, (i + 1) * itemWidth - 5));
            preImage = gameMap[i][j].getId();
            prePostion.set(i, j);
            return;
        }
        //不是第一次点击，前一次点击图片和本次相同
        else if (gameMap[i][j].getId() == preImage) {

            gameMap[i][j].setLocationRect(new Rect(j * itemWidth + 5, i * itemWidth + 5, (j + 1) * itemWidth - 5, (i + 1) * itemWidth - 5));
            //是否preImage和可联通（最多两个拐点）
            if (!isConnected(prePostion, new Point(i, j))) {

                int i1 = prePostion.x;
                int j1 = prePostion.y;
                gameMap[i1][j1].setLocationRect(new Rect(j1 * itemWidth + 10, i1 * itemWidth + 10, (j1 + 1) * itemWidth - 10, (i1 + 1) * itemWidth - 10));
                preImage = gameMap[i][j].getId();
                prePostion.set(i, j);
            }

            return;
        } else {//不相同的话

            //将当前点击图片放大
            gameMap[i][j].setLocationRect(new Rect(j * itemWidth + 5, i * itemWidth + 5, (j + 1) * itemWidth - 5, (i + 1) * itemWidth - 5));
            //前一点击图片复原
            int i1 = prePostion.x;
            int j1 = prePostion.y;
            gameMap[i1][j1].setLocationRect(new Rect(j1 * itemWidth + 10, i1 * itemWidth + 10, (j1 + 1) * itemWidth - 10, (i1 + 1) * itemWidth - 10));

            preImage = gameMap[i][j].getId();
            prePostion.set(i, j);
            return;
        }
    }

    private boolean isConnected(Point p1, Point p2) {


        //没有拐点
        if (zero(p1, p2)) {

            linkList.add(new Point(p1.y, p1.x));
            linkList.add(new Point(p2.y, p2.x));
            return true;
        }
        //一个拐点
        Point pt = one(p1, p2);
        if (pt != null) {

            linkList.add(new Point(p1.y, p1.x));
            linkList.add(new Point(pt.y, pt.x));
            linkList.add(new Point(p2.y, p2.x));
            return true;
        }

        //两个拐点
        List<Point> two = two(p1, p2);
        if (two != null) {

            linkList.add(new Point(p1.y, p1.x));
            linkList.add(new Point(two.get(0).y, two.get(0).x));
            linkList.add(new Point(two.get(1).y, two.get(1).x));
            linkList.add(new Point(p2.y, p2.x));
            return true;
        }
        return false;
    }

    /*
    两个拐点
    两个拐点必定分别位于p1的x或y，p2的x或y
     */
    private List<Point> two(Point p1, Point p2) {

        //保存较小的i，j
        int mini = p2.x, minj = p2.y;
        //保存返回的两个点
        List<Point> list = new ArrayList<>();
        boolean isSwap = false;
        //找到两个点的偏移距离
        int di = p1.x - p2.x;
        int dj = p1.y - p2.y;
        if (di < 0) {

            di = -di;
            mini = p1.x;
        }
        if (dj < 0) {

            dj = -dj;
            minj = p1.y;
        } else {
            //保证P1为左边点
            Point t = p1;
            p1 = p2;
            p2 = t;
            isSwap = true;
        }

        Point pt1;
        Point pt2;
        /*
        |___
            |
        第二根线水平情况
         */
        for (int i = 1; i < di; i++) {

            pt1 = new Point(mini + i, minj);
            pt2 = new Point(mini + i, minj + dj);
            if (gameMap[pt1.x][pt1.y].getIsHide() && gameMap[pt2.x][pt2.y].getIsHide() && zero(p1, pt1) && zero(pt1, pt2) && zero(pt2, p2)) {

                if (isSwap) {
                    list.add(pt2);
                    list.add(pt1);
                } else {
                    list.add(pt1);
                    list.add(pt2);
                }
            }
        }
        /*
        ___
           |
           ---
        第二根线竖直情况
         */
        for (int i = 1; i < dj; i++) {

            pt1 = new Point(mini, minj + i);
            pt2 = new Point(mini + di, minj + i);
            if (gameMap[pt1.x][pt1.y].getIsHide() && gameMap[pt2.x][pt2.y].getIsHide() && zero(p1, pt1) && zero(pt1, pt2) && zero(pt2, p2)) {

                if (isSwap) {
                    list.add(pt2);
                    list.add(pt1);
                } else {
                    list.add(pt1);
                    list.add(pt2);
                }
            }
        }

        /*
        ---
          |
        ---
        同j情况
         */
        for (int i = 0; i < n; i++) {

            pt1 = new Point(p1.x, i);
            pt2 = new Point(p2.x, i);
            if (gameMap[pt1.x][pt1.y].getIsHide() && gameMap[pt2.x][pt2.y].getIsHide() && zero(p1, pt1) && zero(pt1, pt2) && zero(pt2, p2)) {

                if (isSwap) {
                    list.add(pt2);
                    list.add(pt1);
                } else {
                    list.add(pt1);
                    list.add(pt2);
                }
            }
        }

        /*
        |  |
         --
        同i情况
         */
        for (int i = 0; i < n; i++) {

            pt1 = new Point(i, p1.y);
            pt2 = new Point(i, p2.y);
            if (gameMap[pt1.x][pt1.y].getIsHide() && gameMap[pt2.x][pt2.y].getIsHide() && zero(p1, pt1) && zero(pt1, pt2) && zero(pt2, p2)) {

                if (isSwap) {
                    list.add(pt2);
                    list.add(pt1);
                } else {
                    list.add(pt1);
                    list.add(pt2);
                }
            }
        }
        if (list.size() == 0) return null;
        return list;
    }

    //一个拐点
    //一个拐点，那么拐点为(i1,j2)或者(i2,j1)
    private Point one(Point p1, Point p2) {

        //拐点
        Point p3 = new Point(p1.x, p2.y);
        Point p4 = new Point(p2.x, p1.y);

        if (gameMap[p1.x][p2.y].isHide && (zero(p1, p3) && zero(p2, p3))) {

            return p3;
        }
        if (gameMap[p2.x][p1.y].isHide && (zero(p1, p4) && zero(p2, p4))) {

            return p4;
        }
        return null;
    }

    //判断两点之间是否直接联通，没有拐点情况
    private boolean zero(Point p1, Point p2) {

        int i1, i2, j1, j2, i, j;

        if (p1.x > p2.x) {
            i1 = p1.x;
            i2 = p2.x;
        } else {
            i1 = p2.x;
            i2 = p1.x;
        }
        if (p1.y > p2.y) {
            j1 = p1.y;
            j2 = p2.y;
        } else {
            j1 = p2.y;
            j2 = p1.y;
        }
        int dx = i1 - i2;
        int dy = j1 - j2;
        boolean isWin = false;
        //同一直线
        if (p1.x == p2.x) {

            isWin = true;
            for (i = 1; i < dy; i++) {
                if (!gameMap[p1.x][j2 + i].isHide) {
                    isWin = false;
                    break;
                }
            }

        } else if (p1.y == p2.y) {

            isWin = true;
            for (i = 1; i < dx; i++) {
                if (!gameMap[i2 + i][p1.y].isHide) {
                    isWin = false;
                    break;
                }
            }
        }
        return isWin;
    }

    //处理触碰按下后，放开操作
    public void handTouchUpEvent() {

        //如果是第一个点
        if (preImage == -1) return;
        //如果linkList不为空，则需要消除两个可连接的点,即linkList的始末点
        if (!linkList.isEmpty()) {

            gameMap[linkList.get(0).y][linkList.get(0).x].setIsHide(true);
            gameMap[linkList.get(linkList.size() - 1).y][linkList.get(linkList.size() - 1).x].setIsHide(true);
            //将前一个点置为-1
            preImage = -1;
            prePostion = new Point(-1, -1);

            //将linkList置为空
            linkList.clear();
        }
    }

    //重新开始游戏
    public void restart() {

        createGameMap();
        preImage = -1;
        prePostion = new Point(-1, -1);
    }


}
