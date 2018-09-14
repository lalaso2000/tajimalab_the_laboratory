/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gameElements.GameResources;
import java.util.ArrayList;

/**
 * AwardCheckData（通称acd） 夏冬のターン開始時に表彰が取れるかどうかを判定するのに使用する 「自分の手の一覧＝パス」「表彰が取れるかの真偽」「使用するワーカーの数」「使用する研究ポイントの数」「獲得できるスコア」 を保持している
 *
 * @author niwatakumi
 */
public class AwardCheckData implements Comparable<AwardCheckData> {

    private ArrayList<Action> path; // パス
    private int awardable;      // 表彰が取れるかの真偽
    private int workers;            // 使用するワーカーの数
    private int reserchPoint;       // 使用する研究ポイント
    private int score;              // 獲得できるスコア

    /**
     * 初期化コンストラクタ
     */
    public AwardCheckData() {
        path = new ArrayList<>();
        awardable = 1;           // 1 = 勝ち, 0 = 引き分け, -1 = 負け
        workers = 0;
        reserchPoint = 0;
        score = 0;
    }

    /**
     * コピーコンストラクタ
     *
     * @param a
     */
    public AwardCheckData(AwardCheckData a) {
        path = (ArrayList<Action>) a.path.clone();
        awardable = a.awardable;
        workers = a.workers;
        reserchPoint = a.reserchPoint;
        score = a.score;
    }

    /**
     * 最初のアクションを指定した状態でacdを初期化する
     *
     * @param action
     */
    public AwardCheckData(Action action) {
        path = new ArrayList<>();
        awardable = 1;
        workers = 0;
        reserchPoint = 0;
        score = 0;

        // パスを追加
        path.add(action);

        // コマ数を追加
        if (!action.place.equals("1-1")) {
            workers += 1;
        }

        // リサーチポイント、スコア追加
        switch (action.place) {
            case "3-1":
                reserchPoint += 2;
                break;
            case "3-2":
                reserchPoint += 4;
                break;
            case "3-3":
                reserchPoint += 8;
                break;
            case "4-1":
                reserchPoint += 8;
                break;
            case "4-2":
                reserchPoint += 8;
                break;
            case "4-3":
                reserchPoint += 8;
                break;
        }
    }

    /**
     * 既存のacdのパスに新たにアクションを追加したものを作成
     *
     * @param acd
     * @param action
     */
    public AwardCheckData(AwardCheckData acd, Action action) {
        path = (ArrayList<Action>) acd.path.clone();
        awardable = acd.awardable;
        workers = acd.workers;
        reserchPoint = acd.reserchPoint;
        score = acd.score;

        // パスを追加
        path.add(action);

        // コマ数を追加
        if (!action.place.equals("1-1")) {
            workers += 1;
        }

        // リサーチポイント、スコア追加
        switch (action.place) {
            case "3-1":
                reserchPoint += 2;
                break;
            case "3-2":
                reserchPoint += 4;
                break;
            case "3-3":
                reserchPoint += 8;
                break;
            case "4-1":
                reserchPoint += 8;
                break;
            case "4-2":
                reserchPoint += 8;
                break;
            case "4-3":
                reserchPoint += 8;
                break;
        }
    }

    @Override
    public int compareTo(AwardCheckData o) {
        // 表彰が取れるやつが優先
        if (this.awardable > o.awardable) {
            return 4;
        } else if (this.awardable < o.awardable) {
            return -4;
        }

        // 3,4に置くコマが少ないほうが優先
        if (this.workers < o.workers) {
            return 3;
        } else if (this.workers > o.workers) {
            return -3;
        }

        // 使う研究ポイントが少ないほうが優先
        if (this.reserchPoint < o.reserchPoint) {
            return 2;
        } else if (this.reserchPoint > o.reserchPoint) {
            return -2;
        }

        // もらえる点が多いほうが優先
        if (this.score > o.score) {
            return 1;
        } else if (this.score < o.score) {
            return -1;
        }

        // それでも同じなら評価は同じ
        return 0;
    }

    public void addPath(Action a) {
        this.path.add(a);
    }

    public void setAwardable(int b) {
        this.awardable = b;
    }

    public void addWorkers() {
        this.workers += 1;
    }

    public void addReserchPoint(int reserchPoint) {
        this.reserchPoint += reserchPoint;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Action getAction(int index) {
        return this.path.get(index);
    }

    @Override
    public String toString() {
        String str = "path = {";
        for (int i = 0; i < this.path.size(); i++) {
            str += this.path.get(i).worker;
            str += ":";
            str += this.path.get(i).place;
            if (i != this.path.size() - 1) {
                str += " -> ";
            }
        }
        str += "}, ";

        str += "awardable = ";
        str += this.awardable;
        str += ", ";

        str += "workers = ";
        str += this.workers;
        str += ", ";

        str += "reserchPoint = ";
        str += this.reserchPoint;
        str += ", ";

        str += "score = ";
        str += this.score;

        return str;
    }

    public boolean isAwardable() {
        return this.awardable >= 0;
    }

    ArrayList<Action> getPath() {
        return (ArrayList<Action>) path.clone();
    }

}
