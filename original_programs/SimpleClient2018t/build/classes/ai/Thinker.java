/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gameElements.GameResources;

/**
 *
 * @author niwatakumi
 */
public class Thinker {

    private double moneyValue;              // お金の評価値
    private double reserchPointValue;       // 研究ポイントの評価値
    private double scoreValue;              // スコアの評価値
    private double startPlayerValue;        // 先手であることの評価値
    private double trendValue;              // トレンドであることの評価値
    private double employStudentValue;      // 学生を雇用することの評価値
    private double employAssistantValue;    // 助手を雇用することの評価値

    private int mode;           // 現在のモード
    public static final int FREE_STYLE = 0;    // 自由形
    public static final int MONEY_PRIORITY = 1;    // お金稼ぎモード
    public static final int RESERCH_PRIORITY = 2;    // 研究ポイント稼ぎモード
    public static final int SCORE_PRIORITY = 3;    // スコア稼ぎモード
    public static final int MONEY_AND_RESERCH_PRIORITY = 4;    // お金と研究ポイント稼ぎモード

    /**
     * コンストラクタ
     */
    public Thinker() {
        this(1.0, 1.0, 1.0, 1.0, 0, 0, 0);
    }

    /**
     * コンストラクタ(評価値設定付き)
     *
     * @param moneyValue
     * @param reserchPointValue
     * @param scoreValue
     * @param startPlayerValue
     * @param trendValue
     * @param employStudentValue
     * @param employAssistantValue
     */
    public Thinker(double moneyValue, double reserchPointValue, double scoreValue, double startPlayerValue, double trendValue, double employStudentValue, double employAssistantValue) {
        this.moneyValue = moneyValue;
        this.reserchPointValue = reserchPointValue;
        this.scoreValue = scoreValue;
        this.startPlayerValue = startPlayerValue;
        this.trendValue = trendValue;
        this.employStudentValue = employStudentValue;
        this.employAssistantValue = employAssistantValue;
        this.mode = FREE_STYLE;
    }

    /**
     * コンストラクタ(モード指定版)
     *
     * @param mode モード
     */
    public Thinker(int mode) {
        this();
        this.modeChange(mode);
    }

    /**
     * モードチェンジ 各モードの評価値はここで設定
     *
     * @param mode
     */
    private void modeChange(int mode) {
        this.mode = mode;
        switch (mode) {
            case MONEY_PRIORITY:
                this.setMoneyValue(2.0);
                this.setReserchPointValue(1.0);
                this.setScoreValue(1.0);
                this.setStartPlayerValue(1.5);
                this.setTrendValue(0.0);
                this.setEmployStudentValue(0.0);
                this.setEmployAssistantValue(0.0);
                break;
            case RESERCH_PRIORITY:
                this.setMoneyValue(1.0);
                this.setReserchPointValue(2.0);
                this.setScoreValue(1.0);
                this.setStartPlayerValue(1.5);
                this.setTrendValue(0.0);
                this.setEmployStudentValue(0.0);
                this.setEmployAssistantValue(0.0);
                break;
            case SCORE_PRIORITY:
                this.setMoneyValue(1.0);
                this.setReserchPointValue(1.0);
                this.setScoreValue(2.0);
                this.setStartPlayerValue(1.0);
                this.setTrendValue(0.0);
                this.setEmployStudentValue(0.0);
                this.setEmployAssistantValue(0.0);
                break;
            case MONEY_AND_RESERCH_PRIORITY:
                this.setMoneyValue(2.0);
                this.setReserchPointValue(2.0);
                this.setScoreValue(1.0);
                this.setStartPlayerValue(1.5);
                this.setTrendValue(0.0);
                this.setEmployStudentValue(0.0);
                this.setEmployAssistantValue(0.0);
                break;
        }
    }

    /**
     * モードをセットする
     *
     * @param mode
     */
    public void setMode(int mode) {
        this.mode = mode;
        this.modeChange(mode);
    }

    /*  以下、各種評価値のgetterとsetter  */
    /**
     * @return the moneyValue
     */
    public double getMoneyValue() {
        return moneyValue;
    }

    /**
     * @param moneyValue the moneyValue to set
     */
    public void setMoneyValue(double moneyValue) {
        this.moneyValue = moneyValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the reserchPointValue
     */
    public double getReserchPointValue() {
        return reserchPointValue;
    }

    /**
     * @param reserchPointValue the reserchPointValue to set
     */
    public void setReserchPointValue(double reserchPointValue) {
        this.reserchPointValue = reserchPointValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the scoreValue
     */
    public double getScoreValue() {
        return scoreValue;
    }

    /**
     * @param scoreValue the scoreValue to set
     */
    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the startPlayerValue
     */
    public double getStartPlayerValue() {
        return startPlayerValue;
    }

    /**
     * @param startPlayerValue the startPlayerValue to set
     */
    public void setStartPlayerValue(double startPlayerValue) {
        this.startPlayerValue = startPlayerValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the trendValue
     */
    public double getTrendValue() {
        return trendValue;
    }

    /**
     * @param trendValue the trendValue to set
     */
    public void setTrendValue(double trendValue) {
        this.trendValue = trendValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the employStudentValue
     */
    public double getEmployStudentValue() {
        return employStudentValue;
    }

    /**
     * @param employStudentValue the employStudentValue to set
     */
    public void setEmployStudentValue(double employStudentValue) {
        this.employStudentValue = employStudentValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the employAssistantValue
     */
    public double getEmployAssistantValue() {
        return employAssistantValue;
    }

    /**
     * @param employAssistantValue the employAssistantValue to set
     */
    public void setEmployAssistantValue(double employAssistantValue) {
        this.employAssistantValue = employAssistantValue;
        this.mode = FREE_STYLE;
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /*  以上、各種評価値のgetterとsetter  */
    /**
     * 評価関数 ゲームの状態、プレイヤー番号、行動を入れると、その行動に対する評価が返ります。 配置不可能な行動を入力するとnullが返ります。
     *
     * @param gameBoard 現在のゲーム状態
     * @param playerNum 評価したいプレイヤー番号
     * @param action 評価したい行動
     * @return 評価値 or null
     */
    public Double evaluateBoard(Game gameBoard, int playerNum, Action action) {
        // 配置可能かチェック(出来ないならnullを返却)
        if (gameBoard.canPutWorker(playerNum, action.place, action.worker) == false) {
            return null;
        }

        // 各種リソース用変数
        GameResources resource;
        int myMoney;
        int myReserchPoint;
        int myScore;
        int myTotalScore;
        boolean startPlayer;
        int myStudents;
        boolean hasAssistant;

        String season = gameBoard.getSeason(); // 現在の季節
        String trendStr = "T1";    // 現在の季節はトレンドだと何番目か
        switch (season) {
            case "1a":
            case "1b":
            case "4a":
            case "4b":
                trendStr = "T1";
                break;
            case "2a":
            case "2b":
            case "5a":
            case "5b":
                trendStr = "T2";
                break;
            case "3a":
            case "3b":
            case "6a":
            case "6b":
                trendStr = "T3";
                break;
        }

        // ゲームを複製
        Game cloneGame = gameBoard.clone();
        // 打ってみる
        cloneGame.play(playerNum, action.place, action.worker);
        // 季節変わったら季節変わったあとのリソースで評価
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
            // 各種リソースを取得
            resource = cloneGame.getResourcesOf(playerNum);   // リソース取得
            myMoney = resource.getCurrentMoney();                       // 次の季節のお金
            myReserchPoint = resource.getCurrentResrchPoint();          // 次の季節の研究ポイント
            myScore = cloneGame.getScoreOf(trendStr, playerNum);          // 現在の季節のスコア
            myTotalScore = cloneGame.getScore()[playerNum];             // 次の季節の合計スコア
            startPlayer = resource.isStartPlayer();                 // スタートプレイヤーか
            myStudents = resource.getTotalStudentsCount();              // 学生の数
            hasAssistant = resource.hasWorkerOf("A");               // 助手がいるか
        } // 季節が変わらない場合
        else {
            // 各種リソースを取得
            resource = gameBoard.getResourcesOf(playerNum);   // リソース取得
            myMoney = resource.getCurrentMoney();                       // 現在のお金
            myReserchPoint = resource.getCurrentResrchPoint();          // 研究ポイント
            myScore = gameBoard.getScoreOf(trendStr, playerNum);          // 現在の季節のスコア
            myTotalScore = gameBoard.getScore()[playerNum];             // 合計スコア
            startPlayer = resource.isStartPlayer();                 // スタートプレイヤーか
            myStudents = resource.getTotalStudentsCount();              // 学生の数
            hasAssistant = resource.hasWorkerOf("A");               // 助手がいるか

            // 行動で増える要素を加味
            if (action.place.equals("1-1")) {
                if (action.worker.equals("P")) {
                    myReserchPoint += 2;
                }
                if (action.worker.equals("A")) {
                    myReserchPoint += 3;
                }
                if (action.worker.equals("S")) {
                    // ゼミ学生は難しい＆需要ないので省略
                }
            }
            if (action.place.equals("2-1")) {
                myReserchPoint += 3;
                myMoney -= 2;
            }
            if (action.place.equals("2-2")) {
                myReserchPoint += 4;
                myMoney -= 2;
            }
            if (action.place.equals("2-3")) {
                myReserchPoint += 5;
                myMoney -= 2;
            }
            if (action.place.equals("3-1")) {
                myReserchPoint -= 2;
                if (action.worker.equals("S")) {
                    myScore += 2;
                } else {
                    myScore += 1;
                }
            }
            if (action.place.equals("3-2")) {
                myReserchPoint -= 4;
                myMoney -= 1;
                if (action.worker.equals("P")) {
                    myScore += 3;
                } else {
                    myScore += 4;
                }
            }
            if (action.place.equals("3-3")) {
                myReserchPoint -= 8;
                myMoney -= 1;
                if (action.worker.equals("S")) {
                    myScore += 5;
                } else if (action.worker.equals("A")) {
                    myScore += 6;
                } else {
                    myScore += 7;
                }
            }
            if (action.place.equals("4-1")) {
                myReserchPoint -= 8;
                myMoney -= 1;
                if (action.worker.equals("S")) {
                    myScore += 6;
                } else if (action.worker.equals("A")) {
                    myScore += 7;
                } else {
                    myScore += 8;
                }
            }
            if (action.place.equals("4-2")) {
                myReserchPoint -= 8;
                myMoney -= 1;
                if (action.worker.equals("S")) {
                    myScore += 5;
                } else if (action.worker.equals("A")) {
                    myScore += 6;
                } else {
                    myScore += 7;
                }
            }
            if (action.place.equals("4-3")) {
                myReserchPoint -= 8;
                myMoney -= 1;
                if (action.worker.equals("S")) {
                    myScore += 4;
                } else if (action.worker.equals("A")) {
                    myScore += 5;
                } else {
                    myScore += 6;
                }
            }
            if (action.place.equals("5-1")) {
                myMoney += 3;
                startPlayer = true;
            }
            if (action.place.equals("5-2")) {
                myReserchPoint -= 1;
                myMoney += 5;
            }
            if (action.place.equals("5-3")) {
                myReserchPoint -= 3;
                myMoney += 6;
                // トレンド未実装
            }
            if (action.place.equals("6-1")) {
                myReserchPoint -= 3;
                myStudents += 1;
            }
            if (action.place.equals("6-2")) {
                hasAssistant = true;
            }
        }

        // 評価値計算
        Double evaluation = 0.0;
        evaluation += myMoney * this.moneyValue;
        evaluation += myReserchPoint * this.reserchPointValue;
        evaluation += myScore * this.scoreValue;
        if (startPlayer) {
            evaluation += this.startPlayerValue;
        }
        evaluation += myStudents * this.employStudentValue;
        if (hasAssistant) {
            evaluation += this.employAssistantValue;
        }

        return evaluation;
    }

}
