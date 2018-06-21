package ai;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gameElements.Board;
import gameElements.Game;
import gameElements.GameResources;

/**
 *
 * @author niwatakumi
 */
public class Lily2 extends TajimaLabAI {

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

    public static final int PREFETCH_MAX_LEVEL = 4;     // 先読みの最高階数
    
    private static final String[] MONEY_AND_RESERCH_PLACES_NAMES = {"1-1", "2-1", "2-2", "2-3", "5-1", "5-2", "5-3"};
    private static final String[] SCORE_PLACES_NAMES = {"3-1", "3-2", "3-3", "4-1", "4-2", "4-3"};

    private Action[] bestActions;

    /**
     * コンストラクタ
     *
     * @param game
     */
    public Lily2(Game game) {
        super(game);
        // 名前変えておく
        this.myName = "Lily2";
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
                this.scoreValue = 5.0;
                this.startPlayerValue = 1.5;
                this.trendValue = 0.0;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case MONEY_AND_RESERCH_PRIORITY:
                this.moneyValue = 1.0;
                this.reserchPointValue = 2.0;
                this.scoreValue = 5.0;
                this.startPlayerValue = 1.0;
                this.trendValue = 0.5;
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
     * 仮想で打つ
     *
     * @param game 盤面
     * @param playerNum 打つ人
     * @param action アクション
     * @return 打ったあとのゲーム（季節更新済み）
     */
    private Game clonePlay(Game game, int playerNum, Action action) {
        Game cloneGame = game.clone();

        /**
         * この辺テンプレ
         */
        // 配置可能かチェック(出来ないならnullを返却)
        if (cloneGame.canPutWorker(playerNum, action.place, action.worker) == false) {
            return null;
        }

        // アクションしてみる
        cloneGame.play(playerNum, action.place, action.worker);
        if (action.place.equals("5-3")) {
            cloneGame.setTreand(action.trend);
        }
        // 季節が変わるなら更新
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
        }

        return cloneGame;
    }
    
    /**
     * 季節が春秋かどうかをチェックする関数
     * @param game 盤面
     * @return 春秋ならtrue、それ以外はfalse
     */
    private String[] setPlaceArrays(Game game){
        String season = game.getSeason();
        String[] places;
        if(season.contains("a")){
            places = new String[MONEY_AND_RESERCH_PLACES_NAMES.length];
            System.arraycopy(MONEY_AND_RESERCH_PLACES_NAMES, 0, places, 0, MONEY_AND_RESERCH_PLACES_NAMES.length);
        }
        else {
            places = new String[MONEY_AND_RESERCH_PLACES_NAMES.length + SCORE_PLACES_NAMES.length];
            System.arraycopy(MONEY_AND_RESERCH_PLACES_NAMES, 0, places, 0, MONEY_AND_RESERCH_PLACES_NAMES.length);
            System.arraycopy(SCORE_PLACES_NAMES, 0, places, MONEY_AND_RESERCH_PLACES_NAMES.length, SCORE_PLACES_NAMES.length);
        }
        return places;
    }

    private Double prefetchMax(int level, Game game, Action action, Double alpha, Double beta) {
        // 最下層まで読んだら評価値を返す
        if (level == PREFETCH_MAX_LEVEL) {
            Double eva = evaluateBoard(game, this.enemyNumber, action);
            if (eva != null) {
                this.addMessage("(" + level + ") " + action + " -> " + eva);
            }
            return eva;
        }

        // 仮想でゲームを進める（打てないならnull返して終了）
        Game cloneGame = clonePlay(game, this.enemyNumber, action);
        if (cloneGame == null) {
            return null;
        }

        // もし打った手でゲーム終了なら評価を返す
        if (cloneGame.getGameState() == Game.STATE_GAME_END) {
            Double eva = evaluateBoard(game, this.myNumber, action);
            if (eva != null) {
                this.addMessage("(" + level + ") " + action + " -> " + eva);
            }
            return eva;
        }

        // 次のプレイヤーを調べる
        int nextPlayer = cloneGame.getCurrentPlayer();

        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;
        
        // 春秋はスコアを取る場所を除外
        String[] places = this.setPlaceArrays(cloneGame);
        
        // 全手探索
        for (int j = 0; j < places.length; j++) {
            // 全部の場所ループ
            String p = places[j];
            // 5-3の時
            if (p.equals("5-3")) {
                for (int k = 0; k < Game.TREAND_ID_LIST.length; k++) {
                    // 全部のトレンドループ
                    String t = Game.TREAND_ID_LIST[k];
                    for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                        // 全部のワーカーループ
                        String w = GameResources.WORKER_NAMES[i];
                        Action a = new Action(w, p, t);
                        if (nextPlayer == this.myNumber) {
                            eva = this.prefetchMin(level + 1, cloneGame, a, alpha, beta);
                        } else {
                            eva = this.prefetchMax(level + 1, cloneGame, a, alpha, beta);
                        }
                        // bata値を上回ったら探索中止
                        if (eva != null && eva >= beta) {
                            bestEva = eva;
                            this.addMessage("(" + level + ") " + action + " -> " + bestEva);
                            return bestEva;
                        }
                        // 評価良いの見つけたら
                        if (eva != null && eva >= bestEva) {
                            // 更新
                            bestEva = eva;
                            alpha = Double.max(alpha, bestEva);
                        }
                    }
                }
            } else {
                for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                    // 全部のワーカーループ
                    String w = GameResources.WORKER_NAMES[i];
                    Action a = new Action(w, p);
                    if (nextPlayer == this.myNumber) {
                        eva = this.prefetchMin(level + 1, cloneGame, a, alpha, beta);
                    } else {
                        eva = this.prefetchMax(level + 1, cloneGame, a, alpha, beta);
                    }
                    // bata値を上回ったら探索中止
                    if (eva != null && eva >= beta) {
                        bestEva = eva;
                        this.addMessage("(" + level + ") " + action + " -> " + bestEva);
                        return bestEva;
                    }
                    // 評価良いの見つけたら
                    if (eva != null && eva >= bestEva) {
                        // 更新
                        bestEva = eva;
                        alpha = Double.max(alpha, bestEva);
                    }
                }
            }
        }
        this.addMessage("(" + level + ") " + action + " -> " + bestEva);
        return bestEva;
    }

    private Double prefetchMin(int level, Game game, Action action, Double alpha, Double beta) {
        // 最下層まで読んだら評価値を返す
        if (level == PREFETCH_MAX_LEVEL) {
            Double eva = evaluateBoard(game, this.myNumber, action);
            if (eva != null) {
                this.addMessage("(" + level + ") " + action + " -> " + eva);
            }
            return eva;
        }

        // 仮想でゲームを進める（打てないならnull返して終了）
        Game cloneGame = clonePlay(game, this.myNumber, action);
        if (cloneGame == null) {
            return null;
        }

        // もし打った手でゲーム終了なら評価を返す
        if (cloneGame.getGameState() == Game.STATE_GAME_END) {
            Double eva = evaluateBoard(game, this.myNumber, action);
            if (eva != null) {
                this.addMessage("(" + level + ") " + action + " -> " + eva);
            }
            return eva;
        }

        // 次のプレイヤー
        int nextPlayer = cloneGame.getCurrentPlayer();

        // 全手やってみて一番いい手を探す
        Double bestEva = Double.POSITIVE_INFINITY;
        Double eva = 0.0;
        // 春秋はスコアを取る場所を除外
        String[] places = this.setPlaceArrays(cloneGame);
        
        // 全手探索
        for (int j = 0; j < places.length; j++) {
            // 全部の場所ループ
            String p = places[j];
            // 5-3の時
            if (p.equals("5-3")) {
                for (int k = 0; k < Game.TREAND_ID_LIST.length; k++) {
                    // 全部のトレンドループ
                    String t = Game.TREAND_ID_LIST[k];
                    for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                        // 全部のワーカーループ
                        String w = GameResources.WORKER_NAMES[i];
                        Action a = new Action(w, p, t);
                        if (nextPlayer == this.myNumber) {
                            eva = this.prefetchMin(level + 1, cloneGame, a, alpha, beta);
                        } else {
                            eva = this.prefetchMax(level + 1, cloneGame, a, alpha, beta);
                        }
                        // alpha値を下回ったら探索中止
                        if (eva != null && eva <= alpha) {
                            bestEva = eva;
                            this.addMessage("(" + level + ") " + a + " -> " + bestEva);
                            return bestEva;
                        }
                        // 評価良いの見つけたら
                        if (eva != null && eva <= bestEva) {
                            // 更新
                            bestEva = eva;
                            beta = Double.min(beta, bestEva);
                        }
                    }
                }
            } else {
                for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                    // 全部のワーカーループ
                    String w = GameResources.WORKER_NAMES[i];
                    Action a = new Action(w, p);
                    if (nextPlayer == this.myNumber) {
                        eva = this.prefetchMin(level + 1, cloneGame, a, alpha, beta);
                    } else {
                        eva = this.prefetchMax(level + 1, cloneGame, a, alpha, beta);
                    }
                    // alpha値を下回ったら探索中止
                    if (eva != null && eva <= alpha) {
                        bestEva = eva;
                        this.addMessage("(" + level + ") " + a + " -> " + bestEva);
                        return bestEva;
                    }
                    // 評価良いの見つけたら
                    if (eva != null && eva <= bestEva) {
                        // 更新
                        bestEva = eva;
                        beta = Double.min(beta, bestEva);
                    }
                }
            }
        }
        this.addMessage("(" + level + ") " + action + " -> " + bestEva);
        return bestEva;
    }

    /**
     * 考えるフェーズ 手を打つところまで実装
     */
    @Override
    protected void think() {
        this.addMessage("==========================");
        this.addMessage("========== thinking ==========");
        this.addMessage("==========================");

        Action bestAction = null;

        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;
        
        // 春秋はスコアを取らない
        String[] places = this.setPlaceArrays(gameBoard);
        
        // 全手探索
        for (int j = 0; j < places.length; j++) {
            // 全部の場所ループ
            String p = places[j];
            // 5-3の時
            if (p.equals("5-3")) {
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : GameResources.WORKER_NAMES) {
                        // 全部のワーカーループ
                        Action a = new Action(w, p, t);
                        eva = this.prefetchMin(1, gameBoard, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                        // 評価良いの見つけたら
                        if (eva != null && eva > bestEva) {
                            // 更新
                            bestEva = eva;
                            this.addMessage("(" + 0 + ") " + a + " -> " + bestEva);
                            bestAction = a;
                        }
                    }
                }
            } else {
                for (String w : GameResources.WORKER_NAMES) {
                    // 全部のワーカーループ
                    Action a = new Action(w, p);
                    eva = this.prefetchMin(1, gameBoard, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    // 評価良いの見つけたら
                    if (eva != null && eva > bestEva) {
                        // 更新
                        bestEva = eva;
                        this.addMessage("(" + 0 + ") " + a + " -> " + bestEva);
                        bestAction = a;
                    }
                }
            }
        }
        this.addMessage("===========================");
        this.addMessage("========== think end ==========");
        this.addMessage("===========================");

        this.addMessage("* Best Action is " + bestAction + " -> " + bestEva);

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
//    @Override
    protected Double evaluateBoard(Game game, int playerNum, Action action) {
        /**
         * この辺テンプレ
         */
        // 配置可能かチェック(出来ないならnullを返却)
        if (game.canPutWorker(playerNum, action.place, action.worker) == false) {
            return null;
        }

        // アクションする前の季節を取得（表彰を計算するため）
        String seasonStr = game.getSeason();
        // その季節はトレンド番号だと何番目か
        int seasonTrendID = this.convertSeasonToTrend(seasonStr);

        // ゲームを複製
        Game cloneGame = game.clone();
        // アクションしてみる
        cloneGame.play(playerNum, action.place, action.worker);
        if (action.place.equals("5-3")) {
            cloneGame.setTreand(action.trend);
        }
        // 季節が変わるなら更新
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
        }

        // 計算用リソースを取得
        GameResources[] resources = this.getResourcesForEvaluation(cloneGame);

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
         * 評価値の計算
         */
        Double evaluation = 0.0;
        evaluation += calcEvaluate(resources[this.myNumber], seasonTrendID, trendInt);
        evaluation -= calcEvaluate(resources[this.enemyNumber], seasonTrendID, trendInt);

        // スコアの差を計算
        int scoreDiff = resources[this.myNumber].getScoreOf(seasonTrendID) - resources[this.enemyNumber].getScoreOf(seasonTrendID);

        // スコア差でも評価してみる
        if (scoreDiff > 0) {
            evaluation += -(0.25 * scoreDiff) * (0.25 * scoreDiff) + 10.0;
        } else if (scoreDiff == 0) {
            evaluation += 5;
        } else {
            evaluation += -(0.5 * scoreDiff) * (0.5 * scoreDiff) + 0.0;
        }

        return evaluation;
    }

    /**
     * リソースから評価値を計算
     *
     * @param resource リソース
     * @param seasonTrendID 現在の季節
     * @param trendInt トレンドの場所
     * @return
     */
    private Double calcEvaluate(GameResources resource, int seasonTrendID, int trendInt) {
        // リソースに応じて評価値を計算
        Double evaluation = 0.0;
        // 自分のお金×お金の評価値
        evaluation += resource.getCurrentMoney() * this.moneyValue;
        // 自分の研究ポイント×研究ポイントの評価値
        evaluation += resource.getCurrentResrchPoint() * this.reserchPointValue;
        // 現状までのトータルスコア
        evaluation += resource.getTotalScore() * this.scoreValue;
        // 今がトレンドか
        if (seasonTrendID == trendInt) {
            evaluation += this.trendValue;
        }
        // 負の点数は許されない
        if (resource.getTotalScore() < 0) {
            return -100.0;
        }
        // 負債は許されない
        if (resource.getDebt() > 0) {
            return -100.0;
        }
        return evaluation;
    }

    /**
     * 季節が変わった時に呼び出される
     */
    @Override
    protected void seasonChanged() {
//        if (this.gameBoard.getSeason().equals("6b")) {
//            System.out.println("last season.");
//        }
    }

}
