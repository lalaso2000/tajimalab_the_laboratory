import log_tool
import sys
import os

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

# グラフファイルの出力先を取得
output_path = args[2]
if not os.path.isdir(output_path):
    sys.stderr.write('[Error] Output dir is not found. ')
    sys.stderr.write('Please tell the Output dir path.\n')
    exit()

# ファイル名から時刻抽出
file_name_time = log_tool.get_file_name_time(file_path)

# ログファイル読み込み
df = log_tool.read_log(file_path)

# グラフ出力
log_tool.make_graph(df, output_path, file_name_time)
