/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Board;
import gameElements.Game;
import gameElements.GameResources;

/**
 *
 * @author niwatakumi
 */
public class SampleAI extends TajimaLabAI {

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
     *
     * @param game
     */
    public SampleAI(Game game) {
        super(game);
        // 名前変えておく
        this.myName = "SampleAI";
        // 最初はお金と研究ポイントを稼ぐモード
        this.modeChange(MONEY_AND_RESERCH_PRIORITY);

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
                this.moneyValue = 2.0;
                this.reserchPointValue = 1.0;
                this.scoreValue = 1.0;
                this.startPlayerValue = 1.5;
                this.trendValue = 0.0;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case RESERCH_PRIORITY:
                this.moneyValue = 1.0;
                this.reserchPointValue = 2.0;
                this.scoreValue = 1.0;
                this.startPlayerValue = 1.5;
                this.trendValue = 0.0;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case SCORE_PRIORITY:
                this.moneyValue = 1.0;
                this.reserchPointValue = 1.0;
                this.scoreValue = 2.0;
                this.startPlayerValue = 1.5;
                this.trendValue = 0.0;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case MONEY_AND_RESERCH_PRIORITY:
                this.moneyValue = 2.0;
                this.reserchPointValue = 2.0;
                this.scoreValue = 1.0;
                this.startPlayerValue = 1.5;
                this.trendValue = 0.0;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
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
     * 考えるフェーズ 手を打つところまで実装
     */
    @Override
    protected void think() {
        // とりあえず全探索＆最適手を探す
        Action bestAction = new Action("P", "1-1");
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = null;
        for (int j = 0; j < Board.PLACE_NAMES.length; j++) {
            // 全部の場所ループ
            String p = Board.PLACE_NAMES[j];
            // 5-3の時
            if (p.equals("5-3")) {
                for (int k = 0; k < Game.TREAND_ID_LIST.length; k++) {
                    // 全部のトレンドループ
                    String t = Game.TREAND_ID_LIST[k];
                    for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                        // 全部のワーカーループ
                        String w = GameResources.WORKER_NAMES[i];
                        Action a = new Action(w, p, t);
                        eva = this.evaluateBoard(gameBoard, myNumber, a);
                        // 評価良いの見つけたら
                        if (eva != null && eva > bestEva) {
                            // 更新
                            bestEva = eva;
                            bestAction = a;
                        }
                    }
                }
            } else {
                for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                    // 全部のワーカーループ
                    String w = GameResources.WORKER_NAMES[i];
                    Action a = new Action(w, p);
                    eva = this.evaluateBoard(gameBoard, myNumber, a);
                    // 評価良いの見つけたら
                    if (eva != null && eva > bestEva) {
                        // 更新
                        bestEva = eva;
                        bestAction = a;
                    }
                }
            }
        }
        // 最適解を打つ
        this.putWorker(bestAction);
    }

    /**
     * 評価関数
     *
     * @param game アクションする前のゲーム状態
     * @param playerNum アクションする人
     * @param action アクション内容
     * @return 評価値
     */
    @Override
    protected Double evaluateBoard(Game game, int playerNum, Action action) {
        /**
         * この辺テンプレ
         */
        // 配置可能かチェック(出来ないならnullを返却)
        if (gameBoard.canPutWorker(playerNum, action.place, action.worker) == false) {
            return null;
        }

        // アクションしてみた時のリソースを取得
        GameResources[] resources = this.getResources(game, playerNum, action);

        // アクションする前の季節を取得
        String seasonStr = game.getSeason();
        // その季節はトレンド番号だと何番目か
        int seasonTrendID = this.convertSeasonToTrend(seasonStr);

        // トレンドはいつか
        String trendStr = game.getTrend();
        if (action.place.equals("5-3")) {
            trendStr = action.trend;
        }
        // トレンドの数値
        int trendInt = this.convertTrendStrToInt(trendStr);
        /**
         * ここまでテンプレ
         */

        /**
         * こっから評価計算（各自でいじる）
         */
        // リソースに応じて評価値を計算
        Double evaluation = 0.0;
        // 自分のお金×お金の評価値
        evaluation += resources[playerNum].getCurrentMoney() * this.moneyValue;
        // 自分の研究ポイント×研究ポイントの評価値
        evaluation += resources[playerNum].getCurrentResrchPoint() * this.reserchPointValue;
        // 表彰前のスコア×スコアの評価値
        evaluation += resources[playerNum].getScoreOf(seasonTrendID);
        // 現状までのトータルスコア
        // evaluation += resources[playerNum].getTotalScore();
        // スタートプレイヤーかどうか
        if (resources[playerNum].isStartPlayer()) {
            evaluation += this.employStudentValue;
        }
        // 学生の数
        evaluation += resources[playerNum].getTotalStudentsCount();
        // 助手を雇っているか
        if (resources[playerNum].hasWorkerOf("A")) {
            evaluation += this.employAssistantValue;
        }
        // 今がトレンドか
        if (seasonTrendID == trendInt) {
            evaluation += this.trendValue;
        }

        return evaluation;
    }

    /**
     * 季節が変わった時に呼び出される
     */
    @Override
    protected void seasonChanged() {
        // 季節が変わった時に呼び出される
        // 例えば5aの季節になったらモードを切り替える…とか
        if (this.gameBoard.getSeason().equals("5a")) {
            this.setMode(SCORE_PRIORITY);
        }
    }

}
