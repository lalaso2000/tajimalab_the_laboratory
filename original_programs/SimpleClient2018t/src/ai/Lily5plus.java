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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Lily5の強化版<br>
 *
 * 次のような動作をします。
 *
 * <ul>
 * <li>基本：次の春秋の終了を見据えて行動</li>
 * <li>夏冬：1手ごとに表彰を確認</li>
 * <li>最終：最後の夏の始めから秋の終わりを見て行動</li>
 * </ul>
 *
 * 評価関数は次の通り。
 *
 * <ul>
 * <li>基本</li>
 * <ol>
 * <li>学生のコストがあるか</li>
 * <li>負の点数じゃないか</li>
 * <li>総得点に応じて加点</li>
 * <li>8P＋1円があれば加点</li>
 * <li>4P＋1円があれば加点(1回)</li>
 * <li>2P＋1円があれば加点(1回)</li>
 * <li>スタートプレイヤーなら加点</li>
 * </ol>
 * <li>最終</li>
 * <ol>
 * <li>学生のコストがあるか</li>
 * <li>負の点数じゃないか</li>
 * <li>総得点に応じて加点</li>
 * <li>リソースから冬の得点を計算</li>
 * </ol>
 * </ul>
 *
 *
 * @author niwatakumi
 */
public class Lily5plus extends TajimaLabAI {

    private double scoreValue = 5.0;                    // スコアの評価値
    private double startPlayerValue = 0.0;            // スタートプレイヤーの評価値
    private double resourceValue = scoreValue * 0.5;    // 研究ポイントとお金の評価値

    private int mode;   // 現在のモード
    private static final int NORMAL_MODE = 1;   // 基本
    private static final int AWARD_MODE = 2;    // 表彰獲得可能時
    private static final int FINAL_MODE_1 = 3;  // 最終局面(最終夏→秋)
    private static final int FINAL_MODE_2 = 4;  // 最終局面(最終冬)

    private static final int PREFETCH_MAX_LEVEL = 8;    // 先読みの最高階数

    private static final String[] MONEY_AND_RESERCH_PLACES_NAMES = {"1-1", "2-1", "2-2", "2-3", "5-1", "5-2", "5-3"};   // お金と研究ポイントの場所
    private static final String[] FINAL_1_PLACES_NAMES = {"1-1", "2-1", "2-2", "2-3", "5-1", "5-2", "5-3", "3-1", "3-2", "4-1"};  // 最終局面（夏〜秋）に使う場所
    private static final String[] FINAL_2_PLACES_NAMES = {"1-1", "3-1", "3-2", "4-1"};  // 最終局面（冬）に使う場所
    private static final ArrayList<String> AWARD_CHECK_PLACES_NAMES = new ArrayList<>(Arrays.asList("1-1", "3-1", "3-2", "4-1"));   // 表彰獲得可能かチェックするときに使う場所

    private ArrayList<AwardCheckData> awardCheckDatas;  // 夏冬用、acdの一覧
    private ArrayList<Action> awardPath = new ArrayList<>();    // 表彰獲得可能時の最適解

    /**
     * コンストラクタ
     *
     * @param game
     */
    public Lily5plus(Game game) {
        super(game);
        this.myName = "Lily5p v1.02";
        this.mode = NORMAL_MODE;
    }

    /**
     * モードチェンジ 各モードの評価値はここで設定
     *
     * @param mode
     */
    private void modeChange(int mode) {
        this.mode = mode;
        switch (mode) {
            case NORMAL_MODE:
                this.addMessage("mode : NORMAL");
                this.scoreValue = 5.0;
                this.startPlayerValue = 0.0;
                this.resourceValue = 2.5;       // 通常はスコアにしないと評価値は半分
                break;
            case AWARD_MODE:
                this.addMessage("mode : AWARD");
                this.scoreValue = 5.0;
                this.startPlayerValue = 0.0;
                this.resourceValue = 2.5;       // 通常はスコアにしないと評価値は半分
                break;
            case FINAL_MODE_1:
                this.addMessage("mode : FINAL_1");
                this.scoreValue = 1.0;
                this.startPlayerValue = 0.0;
                this.resourceValue = 1.0;       // 最終局面では、スコアに換算する前でもスコアと同じ値
                break;
            case FINAL_MODE_2:
                this.addMessage("mode : FINAL_2");
                this.scoreValue = 1.0;
                this.startPlayerValue = 0.0;
                this.resourceValue = 0.0;
                break;
        }
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
        // ただし論文の場合、次の場所に置いてみる
        if (cloneGame.canPutWorker(playerNum, action.place, action.worker) == false) {
            switch (action.place) {
                case "4-1": {
                    Action a = new Action(action.worker, "4-2");
                    return clonePlay(game, playerNum, a, seasonChangeable);
                }
                case "4-2": {
                    Action a = new Action(action.worker, "3-3");
                    return clonePlay(game, playerNum, a, seasonChangeable);
                }
                case "3-3": {
                    Action a = new Action(action.worker, "4-3");
                    return clonePlay(game, playerNum, a, seasonChangeable);
                }
                default:
                    return null;
            }
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
     * 探索場所を設定
     *
     * @return 探索場所一覧
     */
    private String[] setPlacesList() {
        switch (this.mode) {
            case FINAL_MODE_1:
                return FINAL_1_PLACES_NAMES;
            case FINAL_MODE_2:
                return FINAL_2_PLACES_NAMES;
            default:
                return MONEY_AND_RESERCH_PLACES_NAMES;
        }
    }

    private String[] setWorkerList(Game game) {
        if (this.mode == NORMAL_MODE) {
            if (game.getSeason().contains("a")) {
                GameResources myResources = game.getResourcesOf(this.myNumber);
                if (myResources.hasWorkerOf("P")) {
                    String[] workers = {"P"};
                    return workers;
                } else {
                    String[] workers = {"A", "S"};
                    return workers;
                }
            }
        }
        return GameResources.WORKER_NAMES;
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
            if (cloneGame.getSeason().contains("a")) {
                // 春秋の終わりで評価を返す
                Double eva = evaluateBoard(game, playerNum, action);
                return eva;
            } else {
                // 夏冬は探索続行
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

    /**
     * 先読み中、自分の手を探索する
     *
     * @param level
     * @param game
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMax(int level, Game game, Double alpha, Double beta) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;

        // 探索場所の設定
        String places[] = this.setPlacesList();
        // 探索ワーカーの設定
        String workers[] = this.setWorkerList(game);

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                // 誰かが5-3を打ってない限り飛ばす
                if (game.getTrend().equals("T0")) {
                    continue;
                }
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : workers) {
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
                for (String w : workers) {
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
     * 先読み中、相手の手を探索する
     *
     * @param level
     * @param game
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMin(int level, Game game, Double alpha, Double beta) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.POSITIVE_INFINITY;
        Double eva = 0.0;

        // 探索場所の設定
        String places[] = this.setPlacesList();

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                // 誰かが5-3を打ってない限り飛ばす
                if (game.getTrend().equals("T0")) {
                    continue;
                }
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
     * 先読み関数（表彰獲得可能時） 表彰を取るための手が打てなくならないように手を探索する
     *
     * @param level
     * @param game
     * @param playerNum
     * @param action
     * @param pathIndex
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetch(int level, Game game, int playerNum, Action action, int pathIndex, Double alpha, Double beta) {
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
            if (cloneGame.getSeason().contains("a")) {
                // 春秋の終わりで評価を返す
                Double eva = evaluateBoard(game, playerNum, action);
                return eva;
            } else {
                // 夏冬は探索続行
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
            return this.prefetchMax(level, cloneGame, pathIndex, alpha, beta);
        } else {
            return this.prefetchMin(level, cloneGame, pathIndex, alpha, beta);
        }

    }

    /**
     * 先読み中、自分の手を探索する(表彰獲得可能時)
     *
     * @param level
     * @param game
     * @param pathIndex
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMax(int level, Game game, int pathIndex, Double alpha, Double beta) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;

        // pathを確認
        if (this.awardPath.size() > pathIndex) {
            Action a = this.awardPath.get(pathIndex);
            if (!a.place.equals("1-1")) {
                // aが1-1以外→その手を打つ
                bestEva = this.prefetch(level + 1, game, this.myNumber, a, pathIndex + 1, alpha, beta);
                return bestEva;
            }
        }

        // 探索場所の設定
        String places[] = this.setPlacesList();
        // 探索ワーカーの設定
        String workers[] = this.setWorkerList(game);

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                // 誰かが5-3を打ってない限り飛ばす
                if (game.getTrend().equals("T0")) {
                    continue;
                }
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : workers) {
                        // 全部のワーカーループ
                        Action a = new Action(w, p, t);
                        eva = this.prefetch(level + 1, game, this.myNumber, a, pathIndex + 1, alpha, beta);
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
                for (String w : workers) {
                    // 全部のワーカーループ
                    Action a = new Action(w, p);
                    eva = this.prefetch(level + 1, game, this.myNumber, a, pathIndex + 1, alpha, beta);
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
        // 何置いても置けない場合、bestEvaがNEGATIVE_INFINITYになる
        if (bestEva == Double.NEGATIVE_INFINITY) {
            bestEva = null;
        }
        return bestEva;
    }

    /**
     * 先読み中、相手の手を探索する
     *
     * @param level
     * @param game
     * @param pathIndex
     * @param alpha
     * @param beta
     * @return
     */
    private Double prefetchMin(int level, Game game, int pathIndex, Double alpha, Double beta) {
        // 全手やってみて一番いい手を探す
        Double bestEva = Double.POSITIVE_INFINITY;
        Double eva = 0.0;

        // 探索場所の設定
        String places[] = this.setPlacesList();

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                // 誰かが5-3を打ってない限り飛ばす
                if (game.getTrend().equals("T0")) {
                    continue;
                }
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : GameResources.WORKER_NAMES) {
                        // 全部のワーカーループ
                        Action a = new Action(w, p, t);
                        eva = this.prefetch(level + 1, game, this.enemyNumber, a, pathIndex, alpha, beta);
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
                    eva = this.prefetch(level + 1, game, this.enemyNumber, a, pathIndex, alpha, beta);
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
        // 何置いても置けない場合、bestEvaがPOSITIVE_INFINITYになる
        if (bestEva == Double.POSITIVE_INFINITY) {
            bestEva = null;
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
    protected Double evaluateBoard(Game game, int playerNum, Action action) {
        Double evaluation = 0.0;

        // ゲームを複製
        Game cloneGame = this.clonePlay(game, playerNum, action, true);
        if (cloneGame == null) {
            return null;
        }

        // 計算用リソースを取得
        GameResources[] resources = this.getResourcesForEvaluation(cloneGame);

        // 評価値の計算
        if (this.mode == FINAL_MODE_1 || this.mode == FINAL_MODE_2) {
            boolean isStartPlayer = cloneGame.getStartPlayer() == this.myNumber;
            evaluation += calcEvaluateForFinal(resources, isStartPlayer);
        } else {
            evaluation += calcEvaluate(resources[this.myNumber]);
            evaluation -= calcEvaluate(resources[this.enemyNumber]);
        }
        if (cloneGame.getSeason().contains("b")) {
            // 季節が夏冬の時、スタートプレイヤーにボーナス
            int cp = cloneGame.getCurrentPlayer();
            if (cp == this.myNumber) {
                evaluation += this.startPlayerValue;
            } else {
                evaluation -= this.startPlayerValue;
            }
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
    private Double calcEvaluate(GameResources resource) {
        // リソースに応じて評価値を計算
        Double evaluation = 0.0;

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
        while (reserchPoint / 8 > 0 && money > 0) {
            // 獲得できるスコア×リソースの評価値
            evaluation += 8 * this.resourceValue;
            // 今の計算で使った分差し引き
            reserchPoint -= 8;
            money -= 1;
        }

        // 研究ポイント4点につきお金1円で加点、ただし1回
        int res4 = reserchPoint / 4;
        if (res4 == 1 && money > 0) {
            // 獲得できるスコア(4点)×リソースの評価値
            evaluation += 4 * this.resourceValue;
            // 今の計算で使った分差し引き
            reserchPoint -= 4;
            money -= 1;
        }

        // 研究ポイント2点で加点、これも1回
        int res2 = reserchPoint / 2;
        if (res2 == 1) {
            // 獲得できるスコア(2点)×リソースの評価値
            evaluation += 2 * this.resourceValue;
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

    private Double calcEvaluateForFinal(GameResources[] resources, boolean isStartPlayer) {
        // リソースに応じて評価値を計算
        Double evaluation = 0.0;

        // 自分のリソース
        GameResources myResource = resources[this.myNumber];
        // 相手のリソース
        GameResources enemyResource = resources[this.enemyNumber];

        // スコア差分
        evaluation = (myResource.getTotalScore() - enemyResource.getTotalScore()) * this.scoreValue;

        // 最終局面で何点取れるかを確認
        // お金取得
        int myMoney = myResource.getCurrentMoney();
        int enemyMoney = enemyResource.getCurrentMoney();

        // 最終季節で最低何点取れるかを確認
        // 研究ポイント取得
        int myReserchPoint = myResource.getCurrentResrchPoint();
        int enemyReserchPoint = enemyResource.getCurrentResrchPoint();

        // 人数カウント
        int myWorkerNum = myResource.getNumberofUseableWorkers("P") + myResource.getNumberofUseableWorkers("A") + myResource.getNumberofUseableWorkers("S");
        int enemyWorkerNum = enemyResource.getNumberofUseableWorkers("P") + enemyResource.getNumberofUseableWorkers("A") + enemyResource.getNumberofUseableWorkers("S");

        // 研究ポイント8点につきお金1円で加点
        // ただし2回まで
        int myRes8 = 0;
        while (myReserchPoint / 8 > 0 && myMoney > 0 && myWorkerNum > 0 && myRes8 < 2) {
            // 今の計算で使った分差し引き
            myReserchPoint -= 8;
            myMoney -= 1;
            myWorkerNum -= 1;
            myRes8 += 1;
        }
        int enemyRes8 = 0;
        while (enemyReserchPoint / 8 > 0 && enemyMoney > 0 && enemyWorkerNum > 0 && enemyRes8 < 2) {
            // 今の計算で使った分差し引き
            enemyReserchPoint -= 8;
            enemyMoney -= 1;
            enemyWorkerNum -= 1;
            enemyRes8 += 1;
        }

        // 自分2回論文
        if (myRes8 == 2) {
            switch (enemyRes8) {
                case 2: // 相手が2回
                    // 先手なら+2点
                    if (isStartPlayer) {
                        evaluation += 2.0 * this.resourceValue;
                    } // 後手だと-2点
                    else {
                        evaluation += -2.0 * this.resourceValue;
                    }
                    break;
                case 1: // 相手が1回
                    // 先手なら8+5-7=6点
                    if (isStartPlayer) {
                        evaluation += 6.0 * this.resourceValue;
                    } // 後手なら-8+7+5=4点
                    else {
                        evaluation += 4.0 * this.resourceValue;
                    }
                    break;
                case 0: // 相手が0回
                    // 先手後手関係なく8+5=13点
                    evaluation += 13.0 * this.resourceValue;
                    break;
                default:
                    break;
            }
        }

        // 自分は1回論文
        if (myRes8 == 1) {
            switch (enemyRes8) {
                case 2: // 相手が2回
                    // 先手なら8-7-5=-4点
                    if (isStartPlayer) {
                        evaluation += -4.0 * this.resourceValue;
                    } // 後手だと-8+7-5=-6点
                    else {
                        evaluation += -6.0 * this.resourceValue;
                    }
                    break;
                case 1: // 相手が1回
                    // 先手なら8-7=1点
                    if (isStartPlayer) {
                        evaluation += 1.0 * this.resourceValue;
                    } // 後手なら-8+7=-1点
                    else {
                        evaluation += -1.0 * this.resourceValue;
                    }
                    break;
                case 0: // 相手が0回
                    // 先手後手関係なく8点
                    evaluation += 8.0 * this.resourceValue;
                    break;
                default:
                    break;
            }
        }

        // 自分は0回論文
        if (myRes8 == 0) {
            switch (enemyRes8) {
                case 2: // 相手が2回
                    // -8-5=-13点
                    evaluation += -13.0 * this.resourceValue;
                    break;
                case 1: // 相手が1回
                    // -8点
                    evaluation += -8.0 * this.resourceValue;
                    break;
                case 0: // 相手が0回
                    break;
                default:
                    break;
            }
        }

        // 研究ポイント4点につきお金1円で加点、ただし1回
        int myRes4 = myReserchPoint / 4;
        int enemyRes4 = enemyReserchPoint / 4;
        // S 3-2が置けそう
        if (myRes4 == 1 && myMoney > 0 && myWorkerNum > 0) {
            if (enemyRes4 == 1 && enemyMoney > 0 && enemyWorkerNum > 0) {
                // 相手も置けそうな時
                if (isStartPlayer) {
                    // 先手なら取れる
                    evaluation += 4.0 * this.resourceValue;
                    myWorkerNum -= 1;
                } else {
                    // 後手なら諦める
                    evaluation += -4.0 * this.resourceValue;
                    enemyWorkerNum -= 1;
                }
            } else {
                // 相手が置けなさそうな時
                evaluation += 4.0 * this.resourceValue;
                myWorkerNum -= 1;
            }
        } else {
            if (enemyRes4 == 1 && enemyMoney > 0 && enemyWorkerNum > 0) {
                // 相手だけS 3-2打てそう
                evaluation += -4.0 * this.resourceValue;
                enemyWorkerNum -= 1;
            }
        }

        // 研究ポイント2点で加点、これも1回
        int myRes2 = myReserchPoint / 2;
        int enemyRes2 = enemyReserchPoint / 2;
        // S 3-2が置けそう
        if (myRes2 == 1 && myWorkerNum > 0) {
            if (enemyRes2 == 1 && enemyWorkerNum > 0) {
                // 相手も置けそうな時
                if (isStartPlayer) {
                    // 先手なら取れる
                    evaluation += 2.0 * this.resourceValue;
                    myWorkerNum -= 1;
                } else {
                    // 後手なら諦める
                    evaluation += -2.0 * this.resourceValue;
                    enemyWorkerNum -= 1;
                }
            } else {
                // 相手が置けなさそうな時
                evaluation += 2.0 * this.resourceValue;
                myWorkerNum -= 1;
            }
        } else {
            if (enemyRes2 == 1 && enemyWorkerNum > 0) {
                // 相手だけS 3-2打てそう
                evaluation += -2.0 * this.resourceValue;
                enemyWorkerNum -= 1;
            }
        }

        // 負の点数は許されない
        if (myResource.getTotalScore() < 0) {
            return -1000.0;
        }
        // 負債は許されない
        if (myResource.getDebt() > 0) {
            return -1000.0;
        }

        return evaluation;

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

        // 先手確認
        int currentPlayer = gameBoard.getCurrentPlayer();

        // 表彰可能かどうか調べるオブジェクトを初期化
        this.awardCheckDatas = new ArrayList<>();

        // 自分の全てのパスを決定する
        // 自分のリソースを取得
        GameResources myResources = this.gameBoard.getResourcesOf(this.myNumber);
        // 自分の持ってるワーカーを数える
        ArrayList<String> workersList = this.setMyAllWorker(myResources);
        // お金を取得
        int myMoney = myResources.getCurrentMoney();
        // 研究ポイントを取得
        int myReserchPoint = myResources.getCurrentResrchPoint();

        // 自分のパスを全て抽出してacdの一覧を生成する
        this.setMyAllPath(workersList, AWARD_CHECK_PLACES_NAMES, myMoney, myReserchPoint);

        // acd一覧が表彰獲得可能かを確認する
        for (AwardCheckData acd : this.awardCheckDatas) {
            this.startAwardPrefetch(acd, gameBoard, currentPlayer);
            this.addMessage(acd.toString());
        }

        // awardCheckDatasをソート（最適解を出す）
        Collections.sort(this.awardCheckDatas, Comparator.reverseOrder());
        // もし最適解が表彰獲得可能ならawardPathをセット
        if (this.awardCheckDatas.get(0).isAwardable()) {
            this.awardPath = this.awardCheckDatas.get(0).getPath();
            // あと表彰モードに切り替え
            this.modeChange(AWARD_MODE);
        } else {
            // 最適解が表彰獲得不可ならリストを空にしておく
            this.awardPath = new ArrayList<>();
            // あと通常探索モードに切り替え
            this.modeChange(NORMAL_MODE);
        }

        this.addMessage("BestPath : " + awardCheckDatas.get(0).toString());

        this.addMessage("===========================");
        this.addMessage("========== check end ==========");
        this.addMessage("===========================");

    }

    /**
     * 表彰が取れるかをチェックするため、現在持っているワーカーのリストを生成
     *
     * @return ワーカーの一覧（ArrayList）
     */
    private ArrayList<String> setMyAllWorker(GameResources myResources) {
        ArrayList<String> workersList = new ArrayList<>();

        int professorNum = myResources.getNumberofUseableWorkers("P");
        if (professorNum == 1) {
            workersList.add("P");
        }
        int assistantNum = myResources.getNumberofUseableWorkers("A");
        if (assistantNum == 1) {
            workersList.add("A");
        }
        int studentNum = myResources.getNumberofUseableWorkers("S");
        for (int i = 0; i < studentNum; i++) {
            workersList.add("S");
        }
        return workersList;
    }

    /**
     * 表彰が取れるかをチェックするため、このターンでの表彰関連の全てのパスを抽出する<br>
     *
     * この関数は始めの一手を抽出する関数
     *
     * @param workersList
     * @param placesList
     * @param money
     * @param reserchPoint
     */
    private void setMyAllPath(ArrayList<String> workersList, ArrayList<String> placesList, int money, int reserchPoint) {
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
                if (placesList.get(i).equals("3-1") || placesList.get(i).equals("3-2")) {
                    newPlacesList.remove(i);
                }
                // 必要コストをへらす
                int newMoney = money - moneyCost(placesList.get(i));
                int newReserchPoint = reserchPoint - reserchPointCost(placesList.get(i));
                setMyAllPath(acd, newWorkersList, newPlacesList, newMoney, newReserchPoint);
            }
        }
    }

    /**
     * 表彰が取れるかをチェックするため、このターンでの表彰関連の全てのパスを抽出する<br>
     *
     * この関数は２手目以降を探索するための関数<br>
     *
     * パスが確定したらawardCheckDatasに追加する
     *
     * @param acd
     * @param workersList
     * @param placesList
     * @param money
     * @param reserchPoint
     */
    private void setMyAllPath(AwardCheckData acd, ArrayList<String> workersList, ArrayList<String> placesList, int money, int reserchPoint) {
        // 使えるワーカー一覧がなくなった＝表彰まで見えた
        if (workersList.isEmpty()) {
            // acdを一覧に追加
            this.awardCheckDatas.add(acd);
            return;
        }
        // 自分の手を探索
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
                if (placesList.get(i).equals("3-1") || placesList.get(i).equals("3-2")) {
                    newPlacesList.remove(i);
                }
                // 必要コストをへらす
                int newMoney = money - moneyCost(placesList.get(i));
                int newReserchPoint = reserchPoint - reserchPointCost(placesList.get(i));
                setMyAllPath(newAcd, newWorkersList, newPlacesList, newMoney, newReserchPoint);
            }
        }
    }

    /**
     * 場所に対応するお金のコストを返す関数
     *
     * @param place
     * @return
     */
    private int moneyCost(String place) {
        switch (place) {
            case "1-1":
            case "3-1":
                return 0;
            default:
                return 1;
        }
    }

    /**
     * 場所に対応する研究ポイントを返す関数
     *
     * @param place
     * @return
     */
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

    /**
     * 事前に探索したパス一覧が表彰獲得可能化を先読み開始
     *
     * @param acd
     * @param game
     * @param currentPlayer
     */
    private void startAwardPrefetch(AwardCheckData acd, Game game, int currentPlayer) {
        // 次が自分ならacdの手を使用
        if (currentPlayer == this.myNumber) {
            awardPrefetch(acd, 0, game, this.myNumber, acd.getAction(0));
        } // 次が相手なら相手の手を探索
        else {
            decideEnemyActionForAwardPrefetch(acd, -1, game);
        }
    }

    /**
     * 事前に探索したパス一覧が表彰獲得可能化を先読み
     *
     * @param acd チェックするパスを持つacd
     * @param index パスの何番目か
     * @param game ゲーム盤面
     * @param playerNum プレイヤー番号
     * @param action 実行したい手
     */
    private void awardPrefetch(AwardCheckData acd, int index, Game game, int playerNum, Action action) {
        // すでにacdで表彰が取れないことがわかっているなら探索中止
        if (!acd.isAwardable()) {
            return;
        }

        // 仮想でゲームを進める（打てないならnull返して終了）
        Game cloneGame = clonePlay(game, playerNum, action, false);
        if (cloneGame == null) {
            if (playerNum == this.myNumber) {
                acd.setAwardable(-1);
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

    /**
     * acdのパスが表彰獲得可能かを先読みする際の相手の手の全探索
     *
     * @param acd
     * @param index
     * @param game
     */
    private void decideEnemyActionForAwardPrefetch(AwardCheckData acd, int index, Game game) {
        // 相手側は全探索
        for (String p : AWARD_CHECK_PLACES_NAMES) {
            for (String w : GameResources.WORKER_NAMES) {
                if (!acd.isAwardable()) {
                    return;
                }
                Action a = new Action(w, p);
                this.awardPrefetch(acd, index, game, this.enemyNumber, a);
            }
        }
    }

    /**
     * acdにawardableと獲得得点をセットする
     *
     * @param acd
     * @param game
     */
    private void setAwardable(AwardCheckData acd, Game game) {
        // 今の季節
        String seasonStr = game.getSeason();
        String trendStr = this.convertSeasonToTrendStr(seasonStr);

        // この季節に何点獲得したかチェックする(参考)
        acd.setScore(0);

        // 行動で増える分を加味
        HashMap<String, ArrayList<String>> workers = game.getBoard().getWorkersOnBoard();

        // 自分の番号の文字列
        String myNumberStr = String.valueOf(this.myNumber);

        String key;
        key = "3-1";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.contains(myNumberStr)) {
                if (w.contains("P")) {
                    acd.addScore(1);
                } else if (w.contains("A")) {
                    acd.addScore(1);
                } else if (w.contains("S")) {
                    acd.addScore(2);
                }
            }
        }
        key = "3-2";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.contains(myNumberStr)) {
                if (w.contains("P")) {
                    acd.addScore(3);
                } else if (w.contains("A")) {
                    acd.addScore(4);
                } else if (w.contains("S")) {
                    acd.addScore(4);
                }
            }
        }
        key = "3-3";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.contains(myNumberStr)) {
                if (w.contains("P")) {
                    acd.addScore(7);
                } else if (w.contains("A")) {
                    acd.addScore(6);
                } else if (w.contains("S")) {
                    acd.addScore(5);
                }
            }
        }

        //論文による業績の獲得
        key = "4-1";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.contains(myNumberStr)) {
                if (w.contains("P")) {
                    acd.addScore(8);
                } else if (w.contains("A")) {
                    acd.addScore(7);
                } else if (w.contains("S")) {
                    acd.addScore(6);
                }
            }
        }
        key = "4-2";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.contains(myNumberStr)) {
                if (w.contains("P")) {
                    acd.addScore(7);
                } else if (w.contains("A")) {
                    acd.addScore(6);
                } else if (w.contains("S")) {
                    acd.addScore(5);
                }
            }
        }
        key = "4-3";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.contains(myNumberStr)) {
                if (w.contains("P")) {
                    acd.addScore(6);
                } else if (w.contains("A")) {
                    acd.addScore(5);
                } else if (w.contains("S")) {
                    acd.addScore(4);
                }
            }
        }

        Game cloneGame = game.clone();
        cloneGame.changeNewSeason();

        // リソース
        GameResources myResources = cloneGame.getResourcesOf(this.myNumber);
        GameResources enemyResources = cloneGame.getResourcesOf(this.enemyNumber);

        // 表彰用のスコア
        int myScore = myResources.getSocreOf(trendStr);
        int enemyScore = enemyResources.getSocreOf(trendStr);

        // 表彰がとれるか判定
        if (myScore > enemyScore) {
            acd.setAwardable(1);
        } else if (myScore == enemyScore) {
            acd.setAwardable(0);
        } else {
            acd.setAwardable(-1);
        }
    }

    /**
     * 考えるフェーズ 手を打つところまで実装
     */
    @Override
    protected void think() {

        // 夏冬なら表彰チェック
        if (this.mode == NORMAL_MODE && this.gameBoard.getSeason().contains("b")) {
            this.checkAwardable();
        }

        this.addMessage("==========================");
        this.addMessage("========== thinking ==========");
        this.addMessage("==========================");

        Action bestAction = null;

        // 全手やってみて一番いい手を探す
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = 0.0;

        Action a;

        // 表彰モード＝表彰が取れる
        if (this.mode == AWARD_MODE) {
            // 次の手を取得
            a = this.awardPath.get(0);
            // aが1-1→探索, それ以外→打つ
            if (!a.place.equals("1-1")) {
                // P4-1よりS4-1が先に打たれるのを回避する
                if (a.place.equals("4-1") && a.worker.equals("S")) {
                    int index = 1;
                    while (index < this.awardPath.size()) {
                        Action nextAction = this.awardPath.get(index);
                        if (nextAction.place.equals("4-1") && nextAction.worker.equals("P")) {
                            bestAction = nextAction;
                            this.awardPath.set(index, a);
                            break;
                        }
                        index += 1;
                    }
                    if (bestAction == null) {
                        bestAction = a;
                    }
                } else {
                    bestAction = a;
                }
                bestEva = Double.POSITIVE_INFINITY;

                this.addMessage("===========================");
                this.addMessage("========== think end ==========");
                this.addMessage("===========================");

                this.addMessage("* Best Action is " + bestAction + " -> " + bestEva);

                // 最適解を打つ
                this.putWorker(bestAction);

                // 通常モードに戻す
                this.modeChange(NORMAL_MODE);

                return;
            }

        }

        // 探索場所の設定
        String places[] = this.setPlacesList();
        // 探索ワーカーの設定
        String workers[] = this.setWorkerList(this.gameBoard);

        // 全手探索
        for (String p : places) {
            // 全部の場所ループ
            // 5-3の時
            if (p.equals("5-3")) {
                // 誰かが5-3を打ってない限り飛ばす
                if (this.gameBoard.getTrend().equals("T0")) {
                    continue;
                }
                for (String t : Game.TREAND_ID_LIST) {
                    // 全部のトレンドループ
                    for (String w : workers) {
                        // 全部のワーカーループ
                        a = new Action(w, p, t);
                        if (this.mode == AWARD_MODE) {
                            eva = this.prefetch(1, gameBoard, this.myNumber, a, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                        } else {
                            eva = this.prefetch(1, gameBoard, this.myNumber, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                        }
                        if (eva != null) {
                            this.addMessage(a + " -> " + eva);
                        }
                        // 5-3で評価値が同じ時
                        if (bestAction.place.equals("5-3")) {
                            if (eva != null && Objects.equals(eva, bestEva)) {
                                String trendA = bestAction.trend;
                                String trendB = a.trend;
                                int myScrA = this.gameBoard.getScoreOf(trendA, this.myNumber);
                                int myScrB = this.gameBoard.getScoreOf(trendB, this.myNumber);
                                int enemyScrA = this.gameBoard.getScoreOf(trendA, this.enemyNumber);
                                int enemyScrB = this.gameBoard.getScoreOf(trendB, this.enemyNumber);
                                int scrDiffA = myScrA - enemyScrA;
                                int scrDiffB = myScrB - enemyScrB;
                                if (scrDiffA <= scrDiffB) {
                                    // 勝てそうな方に更新
                                    bestEva = eva;
                                    bestAction = a;
                                }
                            }
                        }
                        // 評価良いの見つけたら
                        else if (eva != null && eva >= bestEva) {
                            // 更新
                            bestEva = eva;
                            bestAction = a;
                        }
                    }
                }
            } else {
                for (String w : workers) {
                    // 全部のワーカーループ
                    a = new Action(w, p);
                    if (this.mode == AWARD_MODE) {
                        eva = this.prefetch(1, gameBoard, this.myNumber, a, 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    } else {
                        eva = this.prefetch(1, gameBoard, this.myNumber, a, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                    }
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

        // PもSも実験に置くとき，Sの方を先に置く（ただしノーマルモード）
        if (this.mode == NORMAL_MODE && bestAction.worker.equals("P") && bestAction.place.contains("2-") && this.gameBoard.getResourcesOf(this.myNumber).hasWorkerOf("S")) {
            bestAction = new Action("S", bestAction.place);
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
        // 毎季節ごとに表彰パスは初期化
        this.awardPath = new ArrayList<>();
        // 季節ごとにモードを変化
        String season = this.gameBoard.getSeason();
        if (season.equals("5b") || season.equals("6a")) {
            this.modeChange(FINAL_MODE_1);
        } else if (season.equals("6b")) {
            this.modeChange(FINAL_MODE_2);
        } else {
            this.modeChange(NORMAL_MODE);
        }
    }

    /**
     * プレイヤー番号が通知された時呼び出される
     */
    @Override
    protected void playerNumDecided() {

    }

}
