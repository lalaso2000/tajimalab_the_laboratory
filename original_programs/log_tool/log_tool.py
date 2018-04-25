import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as tcr
import os
import re


def read_log(file_path):
    return pd.read_csv(file_path, delimiter='\t', index_col=0)


def get_file_name_time(file_name):
    text = os.path.basename(file_name)
    ptn = r'[0-9]+_[0-9]+'
    matchOB = re.search(ptn, text)
    if matchOB:
        return matchOB.group()
    else:
        return None


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


def make_graph(season_df, output_dir, file_name_time):
    # 季節一覧
    season_strs = [
        'start', '1a', '1b', '2a', '2b', '3a', '3b', '4a', '4b', '5a', '5b',
        '6a', '6b', 'end'
    ]

    # お金のデータのみ抽出
    moneys = pd.concat(
        [season_df['player0_Money'], season_df['player1_Money']], axis=1)
    # 研究ポイントのデータのみ抽出
    reserch_points = pd.concat(
        [season_df['player0_ReserchPoint'], season_df['player1_ReserchPoint']],
        axis=1)
    # スコアのデータのみ抽出
    scores = pd.concat(
        [season_df['player0_TotalScore'], season_df['player1_TotalScore']],
        axis=1)

    # グラフのサイズ指定
    plt.rcParams['font.size'] = 8
    # 縦に三つ、横に一つ、x軸を共通にグラフを書く
    fig, axes = plt.subplots(nrows=3, ncols=1, sharex=True, figsize=(6, 8))
    # お金プロット
    moneys.plot(ax=axes[0], title='Money', grid=True)
    # 研究ポイントプロット
    reserch_points.plot(ax=axes[1], title='ReserchPoint', grid=True)
    # スコアプロット
    scores.plot(ax=axes[2], title='Score', grid=True)
    # x軸を季節の名前に置き換え
    plt.xticks(season_df.index, season_strs)
    # y軸の値は整数間隔のみ
    for ax in axes:
        ax.yaxis.set_major_locator(tcr.MaxNLocator(integer=True))
    # pdfファイルで保存
    file_name = output_dir + '/log_' + file_name_time + '_graph.pdf'
    plt.savefig(file_name)
    # 画面に表示
    plt.show()


def main():
    # テスト
    file_path = 'logs/log_20180421_163733_season.tsv'
    file_name_time = get_file_name_time(file_path)
    print(file_name_time)


if __name__ == '__main__':
    main()
