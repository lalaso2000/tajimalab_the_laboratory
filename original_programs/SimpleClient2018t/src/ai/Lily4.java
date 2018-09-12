/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gameElements.GameResources;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.concurrent.WorkerStateEvent;

/**
 *
 * @author niwatakumi
 */
public class Lily4 extends TajimaLabAI {

    private double moneyValue;              // お金の評価値
    private double reserchPointValue;       // 研究ポイントの評価値
    private double scoreValue;              // スコアの評価値
    private double startPlayerValue;        // 先手であることの評価値
    private double trendValue;              // トレンドであることの評価値
    private double employStudentValue;      // 学生を雇用することの評価値
    private double employAssistantValue;    // 助手を雇用することの評価値

    private int mode;           // 現在のモード
    private static final int INIT_MODE = -1;        // 初期化用
    private static final int FREE_STYLE = 0;        // 自由形
    private static final int PLAYER0_MODE = 1;      // player0モード(Lily2.0)
    private static final int PLAYER1_MODE = 2;      // player1モード(Lily2.1)
    private static final int FINAL_MODE = 3;        // 最終局面
    private static final int SCORE_CHECK_MODE = 4;  // 表彰取れるか確認するとき用

    public static final int PREFETCH_MAX_LEVEL = 8;     // 先読みの最高階数

    private static final String[] MONEY_AND_RESERCH_PLACES_NAMES = {"1-1", "2-1", "2-2", "2-3", "5-1", "5-2", "5-3"};
    private static final String[] SCORE_PLACES_NAMES = {"3-1", "3-2", "3-3", "4-1", "4-2", "4-3"};

    /**
     * コンストラクタ
     *
     * @param game
     */
    public Lily4(Game game) {
        super(game);
        // 名前変えておく
        this.myName = "Lily 48+";
        // 最初はお金と研究ポイントを稼ぐモード
        this.modeChange(INIT_MODE);

    }

    /**
     * モードチェンジ 各モードの評価値はここで設定
     *
     * @param mode
     */
    private void modeChange(int mode) {
        this.mode = mode;
        switch (mode) {
            case INIT_MODE:
                this.moneyValue = 1.0;
                this.reserchPointValue = 1.0;
                this.scoreValue = 1.0;
                this.startPlayerValue = 1.0;
                this.trendValue = 1.0;
                this.employStudentValue = 1.0;
                this.employAssistantValue = 1.0;
                break;
            case PLAYER0_MODE:
                this.moneyValue = 1.5;
                this.reserchPointValue = 3.0;
                this.scoreValue = 5.0;
                this.startPlayerValue = 1.0;
                this.trendValue = 0.5;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case PLAYER1_MODE:
                this.moneyValue = 1.5;
                this.reserchPointValue = 3.0;
                this.scoreValue = 5.0;
                this.startPlayerValue = 1.0;
                this.trendValue = 0.5;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case FINAL_MODE:
                this.moneyValue = 1.0;
                this.reserchPointValue = 2.0;
                this.scoreValue = 5.0;
                this.startPlayerValue = 3.0;
                this.trendValue = 0.0;
                this.employStudentValue = 0.0;
                this.employAssistantValue = 0.0;
                break;
            case SCORE_CHECK_MODE:
                this.moneyValue = 0.0;
                this.reserchPointValue = 0.0;
                this.scoreValue = 1.0;
                this.startPlayerValue = 0.0;
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
     * 仮想で打つ
     *
     * @param game 盤面
     * @param playerNum 打つ人
     * @param action アクション
     * @return 打ったあとのゲーム（季節更新済み）
     */
    private Game clonePlay(Game game, int playerNum, Action action) {
        return clonePlay(game, playerNum, action, true);
    }

    /**
     * 仮想で打つ（季節の更新をするかどうか変更可）
     *
     * @param game ゲームボード
     * @param playerNum 次のプレイヤー
     * @param action アクション
     * @param seasonChangeable 季節を更新するかどうか
     * @return 打った盤面
     */
    private Game clonePlay(Game game, int playerNum, Action action, boolean seasonChangeable) {
        Game cloneGame = game.clone();

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
        if (seasonChangeable == true && cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
        }

        return cloneGame;
    }

    /**
     * 季節に応じた探索場所を指定する関数
     *
     * @param game 盤面
     * @return 探索場所一覧
     */
    private String[] setPlaceArrays(Game game) {
        String season = game.getSeason();
        String[] places;
        if (season.contains("a")) {
            // 春秋はお金と研究ポイントの場所のみ
            places = new String[MONEY_AND_RESERCH_PLACES_NAMES.length];
            System.arraycopy(MONEY_AND_RESERCH_PLACES_NAMES, 0, places, 0, MONEY_AND_RESERCH_PLACES_NAMES.length);
        } else {
            // 夏冬は春秋のもの＋スコアの場所
            places = new String[MONEY_AND_RESERCH_PLACES_NAMES.length + SCORE_PLACES_NAMES.length];
            System.arraycopy(MONEY_AND_RESERCH_PLACES_NAMES, 0, places, 0, MONEY_AND_RESERCH_PLACES_NAMES.length);
            System.arraycopy(SCORE_PLACES_NAMES, 0, places, MONEY_AND_RESERCH_PLACES_NAMES.length, SCORE_PLACES_NAMES.length);
        }
        return places;
    }

    /**
     * 先読み関数
     *
     * @param level 先読みの階層
     * @param game 現在のゲーム場面
     * @param playerNum 次にプレイする人
     * @param action 次のアクション
     * @param alpha アルファ値
     * @param beta ベータ値
     * @return 評価値
     */
    private Double prefetch(int level, Game game, int playerNum, Action action, Double alpha, Double beta) {
        // 最下層まで読んだら評価値を返す
        if (level == PREFETCH_MAX_LEVEL) {
            Double eva = evaluateBoard(game, playerNum, action);
            return eva;
        }

        // 仮想でゲームを進める（打てないならnull返して終了）
        Game cloneGame = clonePlay(game, playerNum, action, false);
        if (cloneGame == null) {
            return null;
        }

        // もし打った手でゲーム終了なら評価を返す
        if (cloneGame.getGameState() == Game.STATE_GAME_END) {
            Double eva = evaluateBoard(game, playerNum, action);
            return eva;
        }

        double extraEva = 0.0;
        // 季節の更新がある場合
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            // 夏冬の終わりなら、表彰が取れているかチェック
            String season = cloneGame.getSeason();
            if (season.contains("b")) {
                // 表彰のスコアを見る
                String seasonTrendStr = this.convertSeasonToTrendStr(season);
                int myScore = cloneGame.getScoreOf(seasonTrendStr, this.myNumber);
                int enemyScore = cloneGame.getScoreOf(seasonTrendStr, this.enemyNumber);
                // 表彰が取れているなら評価値にボーナス
                if (myScore >= enemyScore) {
                    extraEva = 100.0;
                } else {
                    extraEva = -100.0;
                }
            }
            // 季節を更新
            cloneGame.changeNewSeason();
        }

        // 次のプレイヤーを調べる
        int nextPlayer = cloneGame.getCurrentPlayer();
        // 次の手を探索
        if (nextPlayer == this.myNumber) {
            return this.prefetchMax(level, cloneGame, action, alpha, beta);
        } else {
            return this.prefetchMin(level, cloneGame, action, alpha, beta);
        }

    }

    /**
     * 自分の手を探す<br>
     *
     * prefetchから呼び出して使う
     *
     * @param level
     * @param game
     * @param action
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMax(int level, Game game, Action action, Double alpha, Double beta) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;

        // 春秋はスコアを取る場所を除外
        String[] places = this.setPlaceArrays(game);

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : GameResources.WORKER_NAMES) {
                        // 全部のワーカーループ
                        Action a = new Action(w, p, t);
                        eva = this.prefetch(level + 1, game, this.myNumber, a, alpha, beta);
                        // bata値を上回ったら探索中止
                        if (eva != null && eva >= beta) {
                            bestEva = eva;
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
                for (String w : GameResources.WORKER_NAMES) {
                    // 全部のワーカーループ
                    Action a = new Action(w, p);
                    eva = this.prefetch(level + 1, game, this.myNumber, a, alpha, beta);
                    // bata値を上回ったら探索中止
                    if (eva != null && eva >= beta) {
                        bestEva = eva;
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
        return bestEva;
    }

    /**
     * 相手の手を探す<br>
     *
     * prefetchから呼び出して使う
     *
     * @param level
     * @param game
     * @param action
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMin(int level, Game game, Action action, Double alpha, Double beta) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.POSITIVE_INFINITY;
        Double eva = 0.0;
        // 春秋はスコアを取る場所を除外
        String[] places = this.setPlaceArrays(game);

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : GameResources.WORKER_NAMES) {
                        // 全部のワーカーループ
                        Action a = new Action(w, p, t);
                        eva = this.prefetch(level + 1, game, this.enemyNumber, a, alpha, beta);
                        // alpha値を下回ったら探索中止
                        if (eva != null && eva <= alpha) {
                            bestEva = eva;
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
                for (String w : GameResources.WORKER_NAMES) {
                    // 全部のワーカーループ
                    Action a = new Action(w, p);
                    eva = this.prefetch(level + 1, game, this.enemyNumber, a, alpha, beta);
                    // alpha値を下回ったら探索中止
                    if (eva != null && eva <= alpha) {
                        bestEva = eva;
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
        return bestEva;
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
        Double evaluation = 0.0;

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
        int seasonTrendID = this.convertSeasonToTrendInt(seasonStr);

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
        evaluation += calcEvaluate(resources[this.myNumber], seasonTrendID, trendInt);
        evaluation -= calcEvaluate(resources[this.enemyNumber], seasonTrendID, trendInt);

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
//        // 自分のお金×お金の評価値
//        evaluation += resource.getCurrentMoney() * this.moneyValue;
//        // 自分の研究ポイント×研究ポイントの評価値
//        evaluation += resource.getCurrentResrchPoint() * this.reserchPointValue;
        // 現状までのトータルスコア
        evaluation += resource.getTotalScore() * this.scoreValue;

        // 研究ポイントと得点がいい感じになっていたら加点
        // お金取得、ただし学生の支払うコストを差し引いておく
        int money = resource.getCurrentMoney() - resource.getTotalStudentsCount();
        if (money < 0) {
            // え、学生のコストでお金なくなるの…
            return -1000.0;
        }
        // 研究ポイント取得
        int reserchPoint = resource.getCurrentResrchPoint();

        // 研究ポイント8点につきお金1円で加点
        int res8 = reserchPoint / 8;
        if (res8 < money) {
            // 獲得できるスコア×スコアの評価値×0.7(ちょい低め)
            evaluation += res8 * 8 * this.scoreValue * 0.5;
            // 今の計算で使った分差し引き
            reserchPoint -= res8 * 8;
            money -= res8;
        }

        // 研究ポイント4点につきお金1円で加点、ただし1回
        int res4 = reserchPoint / 4;
        if (res4 == 1 && money != 0) {
            // 獲得できるスコア(4点)×スコアの評価値×0.7(ちょい低め)
            evaluation += 4 * this.scoreValue * 0.5;
            // 今の計算で使った分差し引き
            reserchPoint -= 4;
            money -= 1;
        }

        // 研究ポイント2点で加点、これも1回
        int res2 = reserchPoint / 2;
        if (res2 == 1) {
            // 獲得できるスコア(2点)×スコアの評価値×0.7(ちょい低め)
            evaluation += 2 * this.scoreValue * 0.5;
            // 今の計算で使った分差し引き
            reserchPoint -= 2;
        }

        // 負の点数は許されない
        if (resource.getTotalScore() < 0) {
            return -1000.0;
        }
        // 負債は許されない
        if (resource.getDebt() > 0) {
            return -1000.0;
        }
        return evaluation;
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
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : GameResources.WORKER_NAMES) {
                        // 全部のワーカーループ
                        Action a = new Action(w, p, t);
                        eva = this.prefetch(1, gameBoard, this.myNumber, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                        if (eva != null) {
                            this.addMessage(a + " -> " + eva);
                        }
                        // 評価良いの見つけたら
                        if (eva != null && eva >= bestEva) {
                            // 更新
                            bestEva = eva;
                            bestAction = a;
                        }
                    }
                }
            } else {
                for (String w : GameResources.WORKER_NAMES) {
                    // 全部のワーカーループ
                    Action a = new Action(w, p);
                    eva = this.prefetch(1, gameBoard, this.myNumber, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    if (eva != null) {
                        this.addMessage(a + " -> " + eva);
                    }
                    // 評価良いの見つけたら
                    if (eva != null && eva >= bestEva) {
                        // 更新
                        bestEva = eva;
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
     * 季節が変わった時に呼び出される
     */
    @Override
    protected void seasonChanged() {
        if (this.gameBoard.getSeason().equals("6b")) {
            this.modeChange(FINAL_MODE);
        }
    }

    /**
     * プレイヤー番号が通知された時呼び出される
     */
    @Override
    protected void playerNumDecided() {
        if (this.myNumber == 0) {
            this.modeChange(PLAYER0_MODE);
        } else {
            this.modeChange(PLAYER1_MODE);
        }
    }

}
