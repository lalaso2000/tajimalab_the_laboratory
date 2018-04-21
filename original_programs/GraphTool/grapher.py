import sys
import os
import pandas as pd
import matplotlib.pyplot as plt


def make_season_log(row_df, output_dir, file_name_time):
    # 季節一覧
    season_strs = [
        '1a', '1b', '2a', '2b', '3a', '3b', '4a', '4b', '5a', '5b', '6a', '6b'
    ]
    # スタート状態を追加
    season_df = row_df[row_df['Season'] == 'start']

    for s in season_strs:
        # 各季節の最後の状態を追加
        series = row_df[row_df['Season'] == s].tail(1)
        season_df = pd.concat([season_df, series])

    # ゲーム終了時の状態を追加
    season_df = pd.concat([season_df, row_df[row_df['Season'] == 'end']])

    # indexをリセット(先頭列の連番)
    season_df = season_df.reset_index()

    # 要らない情報を削除
    season_df = season_df.drop(
        [
            'player0_P', 'player0_A', 'player0_S', 'player0_StudentCount',
            'player1_P', 'player1_A', 'player1_S', 'player1_StudentCount'
        ],
        axis=1)

    # NaNに'null'を代入
    season_df = season_df.fillna('null')

    # csvファイルで出力
    file_name = output_dir + '/log_' + file_name_time + '_season.tsv'
    season_df.to_csv(file_name, sep='\t')

    return season_df


def make_graph(season_df):
    # お金のデータのみ抽出
    moneys = pd.concat(
        [
            season_df['Season'], season_df['player0_Money'],
            season_df['player1_Money']
        ],
        axis=1)
    # 研究ポイントのデータのみ抽出
    reserch_points = pd.concat(
        [
            season_df['Season'], season_df['player0_ReserchPoint'],
            season_df['player1_ReserchPoint']
        ],
        axis=1)
    # スコアのデータのみ抽出
    scores = pd.concat(
        [
            season_df['Season'], season_df['player0_TotalScore'],
            season_df['player1_TotalScore']
        ],
        axis=1)
    # グラフのサイズ指定
    plt.figure(figsize=(6, 8))
    plt.rcParams['font.size'] = 8
    # 縦に三つ、横に一つ、x軸を共通にグラフを書く
    fig, axes = plt.subplots(nrows=3, ncols=1, sharex=True)
    # お金プロット
    moneys.plot(x=moneys['Season'], ax=axes[0], title='Money', grid=True)
    # 研究ポイントプロット
    reserch_points.plot(
        x=reserch_points['Season'],
        ax=axes[1],
        title='ReserchPoint',
        grid=True)
    # スコアプロット
    scores.plot(x=scores['Season'], ax=axes[2], title='Score', grid=True)
    # 画面に表示
    plt.show()


# コマンドライン引数取得
args = sys.argv

# コマンドライン引数の数がおかしい
if len(args) != 3:
    sys.stderr.write('[Error] Log file is not specified. ')
    sys.stderr.write('Please tell the Log File path and Output dir path.\n')
    exit()

# ログファイルのパスを取得
file_path = args[1]
# ログファイルが見つからない
if not os.path.isfile(file_path):
    sys.stderr.write('[Error] Log file is not found. ')
    sys.stderr.write('Please tell the Log File path.\n')
    exit()

# ログファイルの出力先を取得
output_path = args[2]
if not os.path.isdir(output_path):
    sys.stderr.write('[Error] Output dir is not found. ')
    sys.stderr.write('Please tell the Output dir path.\n')
    exit()

file_name_time = file_path[-23:-8]

# ログファイル読み込み
row_df = pd.read_csv(file_path, delimiter='\t', index_col=0)

# 季節ごとのログを作る
season_df = make_season_log(row_df, output_path, file_name_time)

# グラフ出力
make_graph(season_df)
