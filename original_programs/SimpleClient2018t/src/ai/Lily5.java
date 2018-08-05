/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gameElements.GameResources;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static javafx.scene.input.KeyCode.T;

/**
 *
 * @author niwatakumi
 */
public class Lily5 extends TajimaLabAI {

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
    private static final String[] AWARD_CHECK_PLACES_NAMES = {"1-1", "3-1", "3-2", "3-3", "4-1", "4-2", "4-3"};

    private ArrayList<AwardCheckData> awardCheckDatas;

    /**
     * コンストラクタ
     *
     * @param game
     */
    public Lily5(Game game) {
        super(game);
        // 名前変えておく
        this.myName = "Lily5(award stop)";
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
        if (seasonChangeable == true && cloneGame.getGameState() == Game.STATE_SEASON_END) {
            cloneGame.changeNewSeason();
        }

        return cloneGame;
    }

    /**
     * 季節に応じた探索場所を返す関数
     *
     * @param game 盤面
     * @return 探索場所の配列
     */
    private String[] setPlaceArrays(Game game) {
        String season = game.getSeason();
        String[] places;
        if (season.contains("a")) {
            places = new String[MONEY_AND_RESERCH_PLACES_NAMES.length];
            System.arraycopy(MONEY_AND_RESERCH_PLACES_NAMES, 0, places, 0, MONEY_AND_RESERCH_PLACES_NAMES.length);
        } else {
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
     * @return
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

        // もし打った手で季節が変わるとき
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            String season = cloneGame.getSeason();
            if (season.contains("b")) {
                // 夏冬なら評価を返す
                Double eva = evaluateBoard(game, playerNum, action);
                return eva;
            } else {
                // 春秋なら季節更新→探索続行
                cloneGame.changeNewSeason();
            }
        }

        // もし打った手でゲーム終了なら評価を返す
        if (cloneGame.getGameState() == Game.STATE_GAME_END) {
            Double eva = evaluateBoard(game, playerNum, action);
            return eva;
        }

        // 次のプレイヤーを調べる
        int nextPlayer = cloneGame.getCurrentPlayer();
        // 次の手を探索
        if (nextPlayer == this.myNumber) {
            return this.prefetchMax(level, cloneGame, alpha, beta);
        } else {
            return this.prefetchMin(level, cloneGame, alpha, beta);
        }

    }

    private Double prefetchMax(int level, Game game, Double alpha, Double beta) {
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
//                            this.addMessage("(" + level + ") " + action + " -> " + bestEva);
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
//                        this.addMessage("(" + level + ") " + action + " -> " + bestEva);
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
//        this.addMessage("(" + level + ") " + action + " -> " + bestEva);
        return bestEva;
    }

    private Double prefetchMin(int level, Game game, Double alpha, Double beta) {
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
//                            this.addMessage("(" + level + ") " + a + " -> " + bestEva);
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
//                        this.addMessage("(" + level + ") " + a + " -> " + bestEva);
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
//        this.addMessage("(" + level + ") " + action + " -> " + bestEva);
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
        // 夏冬の最初に表彰が取れるかどうかをチェック
        if (this.gameBoard.getSeason().contains("b")) {
            this.checkAwardable();
        }
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

    /**
     * 夏と冬のはじめに表彰が取れるかをチェックする
     *
     * @return 表彰取れるかどうか
     */
    private void checkAwardable() {
        this.addMessage("==========================");
        this.addMessage("======== award check ========");
        this.addMessage("==========================");

        // 確認モードに切り替え
        int beforeMode = this.mode;
        this.modeChange(SCORE_CHECK_MODE);

        // 先手確認
        int currentPlayer = gameBoard.getCurrentPlayer();

        // 表彰可能かどうか調べるオブジェクトを初期化
        this.awardCheckDatas = new ArrayList<>();

        // 自分の全ての手を決定する
        // 自分の持ってるワーカーを数える
        ArrayList<String> workersList = new ArrayList<>();
        GameResources myResources = this.gameBoard.getResourcesOf(this.myNumber);
        workersList.add("P");
        int assistantNum = myResources.getNumberofUseableWorkers("A");
        if (assistantNum == 1) {
            workersList.add("A");
        }
        int studentNum = myResources.getNumberofUseableWorkers("S");
        for (int i = 0; i < studentNum; i++) {
            workersList.add("S");
        }
        // アクション一覧を生成
        // asListは固定長しか返せない
        List<String> tmp = Arrays.asList(AWARD_CHECK_PLACES_NAMES);
        ArrayList<String> placesList = new ArrayList<>(tmp);
        // お金を取得
        int myMoney = myResources.getCurrentMoney();
        // 研究ポイントを取得
        int myReserchPoint = myResources.getCurrentResrchPoint();

        // 自分の手を全て抽出
        this.setMyAllAction(workersList, placesList, myMoney, myReserchPoint);

        // 抽出した手を確認する
        for (AwardCheckData acd : this.awardCheckDatas) {
            this.startAwardPrefetch(acd, gameBoard, currentPlayer);
            this.addMessage(acd.toString());
        }

        // awardCheckDatasをソート（最適解を出す）
        Collections.sort(awardCheckDatas, Comparator.reverseOrder());
        this.addMessage("BestPath : " + awardCheckDatas.get(0).toString());

        this.addMessage("===========================");
        this.addMessage("========== check end ==========");
        this.addMessage("===========================");

        // 通常モードに戻す
        this.modeChange(beforeMode);
    }

    private void setMyAllAction(ArrayList<String> workersList, ArrayList<String> placesList, int money, int reserchPoint) {
        for (int j = 0; j < workersList.size(); j++) {
            for (int i = 0; i < placesList.size(); i++) {
                // 必要コストが支払えない場合はスキップ
                if (money < moneyCost(placesList.get(i))) {
                    continue;
                }
                if (reserchPoint < reserchPointCost(placesList.get(i))) {
                    continue;
                }
                // acdのactionに追加
                Action a = new Action(workersList.get(j), placesList.get(i));
                // acd初期化
                AwardCheckData acd = new AwardCheckData(a);
                // 次のアクションを追加しに行く
                ArrayList<String> newWorkersList = new ArrayList<>(workersList);
                newWorkersList.remove(j);
                ArrayList<String> newPlacesList = new ArrayList<>(placesList);
                if (!placesList.get(i).equals("1-1")) {
                    newPlacesList.remove(i);
                }
                // 必要コストをへらす
                int newMoney = money - moneyCost(placesList.get(i));
                int newReserchPoint = reserchPoint - reserchPointCost(placesList.get(i));
                setMyAllAction(acd, newWorkersList, newPlacesList, newMoney, newReserchPoint);
            }
        }
    }

    private void setMyAllAction(AwardCheckData acd, ArrayList<String> workersList, ArrayList<String> placesList, int money, int reserchPoint) {
        if (workersList.isEmpty()) {
            this.awardCheckDatas.add(acd);
            return;
        }
        for (int j = 0; j < workersList.size(); j++) {
            for (int i = 0; i < placesList.size(); i++) {
                // 必要コストが支払えない場合はスキップ
                if (money < moneyCost(placesList.get(i))) {
                    continue;
                }
                if (reserchPoint < reserchPointCost(placesList.get(i))) {
                    continue;
                }
                // acdのactionに追加
                Action a = new Action(workersList.get(j), placesList.get(i));
                AwardCheckData newAcd = new AwardCheckData(acd, a);
                // 次のアクションを追加しに行く
                ArrayList<String> newWorkersList = new ArrayList<>(workersList);
                newWorkersList.remove(j);
                ArrayList<String> newPlacesList = new ArrayList<>(placesList);
                if (!placesList.get(i).equals("1-1")) {
                    newPlacesList.remove(i);
                }
                // 必要コストをへらす
                int newMoney = money - moneyCost(placesList.get(i));
                int newReserchPoint = reserchPoint - reserchPointCost(placesList.get(i));
                setMyAllAction(newAcd, newWorkersList, newPlacesList, newMoney, newReserchPoint);
            }
        }
    }

    private int moneyCost(String place) {
        switch (place) {
            case "1-1":
            case "3-1":
                return 0;
            default:
                return 1;
        }
    }

    private int reserchPointCost(String place) {
        switch (place) {
            case "1-1":
                return 0;
            case "3-1":
                return 2;
            case "3-2":
                return 4;
            default:
                return 8;
        }
    }

    private void startAwardPrefetch(AwardCheckData acd, Game game, int currentPlayer) {
        // 次が自分ならacdの手を使用
        if (currentPlayer == this.myNumber) {
            awardPrefetch(acd, 0, game, this.myNumber, acd.getAction(0));
        } // 次が相手なら相手の手を探索
        else {
            decideEnemyActionForAwardPrefetch(acd, -1, game);
        }
    }

    private void awardPrefetch(AwardCheckData acd, int index, Game game, int playerNum, Action action) {
        // すでにacdで表彰が取れないことがわかっているなら探索中止
        if(!acd.isAwardable()){
            return;
        }
        
        // 仮想でゲームを進める（打てないならnull返して終了）
        Game cloneGame = clonePlay(game, playerNum, action, false);
        if (cloneGame == null) {
            if (playerNum == this.myNumber) {
                acd.setAwardable(false);
            }
            return;
        }

        // もし打った手で季節が変わるとき
        if (cloneGame.getGameState() == Game.STATE_SEASON_END) {
            String season = cloneGame.getSeason();
            if (season.contains("b")) {
                // 夏冬なら表彰をチェック
                this.setAwardable(acd, cloneGame);
                return;
            } else {
                // 春秋なら季節更新→探索続行
                cloneGame.changeNewSeason();
            }
        }

        // もし打った手でゲーム終了なら評価を返す
        if (cloneGame.getGameState() == Game.STATE_GAME_END) {
            this.setAwardable(acd, cloneGame);
            return;
        }

        // 次のプレイヤーを調べる
        int nextPlayer = cloneGame.getCurrentPlayer();
        // 次の手を探索
        if (nextPlayer == this.myNumber) {
            awardPrefetch(acd, index + 1, cloneGame, this.myNumber, acd.getAction(index + 1));
        } else {
            decideEnemyActionForAwardPrefetch(acd, index, cloneGame);
        }
    }

    private void decideEnemyActionForAwardPrefetch(AwardCheckData acd, int index, Game game) {
        // 相手側は全探索
        for (String p : AWARD_CHECK_PLACES_NAMES) {
            for (String w : GameResources.WORKER_NAMES) {
                if(!acd.isAwardable()) return;
                Action a = new Action(w, p);
                this.awardPrefetch(acd, index, game, this.enemyNumber, a);
            }
        }
    }

    private void setAwardable(AwardCheckData acd, Game game) {
        // 今の季節
        String seasonStr = game.getSeason();
        String trendStr = this.convertSeasonToTrendStr(seasonStr);
        
        Game cloneGame = game.clone();
        cloneGame.changeNewSeason();

        // リソース
        GameResources myResources = cloneGame.getResourcesOf(this.myNumber);
        GameResources enemyResources = cloneGame.getResourcesOf(this.enemyNumber);

        // 表彰用のスコア
        int myScore = myResources.getSocreOf(trendStr);
        int enemyScore = enemyResources.getSocreOf(trendStr);

        // 表彰がとれるか判定
        if (myScore >= enemyScore) {
            acd.setAwardable(true);
        } else {
            acd.setAwardable(false);
        }
    }

}
