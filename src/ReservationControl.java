import java.awt.Button;
import java.awt.Dialog;
import java.sql.*;
import java.util.Calendar;
import java.util.ArrayList;


public class ReservationControl {

		//MySQLデータベース接続のための変数
		Connection sqlCon;
		Statement sqlStmt;
		String sql_userid = "reservation_user";	//ユーザID
		String sql_password = "pass0004";	//パスワード

		//この予約システムのユーザIDとログイン状態
		public String reservation_userid;
		private boolean flagLogin;
		

		ReservationControl(){
			flagLogin = false;
		}

		public  boolean getFlagLogin() {
			return  flagLogin;
		}



		//データベースの操作準備
		private void connectDB(){
			try{
					// ドライバクラスをロード
					Class.forName("org.gjt.mm.mysql.Driver"); // MySQLの場合

					// データベースへ接続
					String url = "jdbc:mysql://localhost/db_reservation?useSSL=false";
				  sqlCon = DriverManager.getConnection(url, sql_userid, sql_password);

					// ステートメントオブジェクトを生成
					sqlStmt = sqlCon.createStatement();
			} catch (Exception e) {
						e.printStackTrace();
				}
		}

		//データベースの切断
		private void closeDB(){
			try{
				sqlStmt.close();
				sqlCon.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		////指定した日，施設の 空き状況(というか予約状況)
		public String getReservationOn( String facility, String ryear_str, String rmonth_str, String rday_str){

			String res = "";

			// 年月日が数字かどうかををチェックする処理
			try {
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);
			

			res =	facility + "/" + ryear + "/" + rmonth + "/" + rday + "  予約状況\n\n";

			// 月と日が一桁だったら，前に0をつける処理
			if (rmonth_str.length()==1) {
				rmonth_str = "0" + rmonth_str;
			}
			if (rday_str.length()==1){
				rday_str = "0" + rday_str;
			}
			//SQLで検索するための年月日のフォーマットの文字列を作成する処理
			String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

			//(1) MySQLを使用する準備
			connectDB();

			//(2) MySQLの操作(SELECT文の実行)
			try {
						// 予約情報を取得するクエリ
						String sql = "SELECT * FROM db_reservation.reservation WHERE date ='" + rdate + "' AND facility_name = '"+ facility +"' ORDER BY start_time;";
						// クエリーを実行して結果セットを取得
						ResultSet rs = sqlStmt.executeQuery(sql);
						// 検索結果から予約状況を作成
						boolean exist = false;
						while(rs.next()){
								String start = rs.getString("start_time");
								String end = rs.getString("end_time");
								res += "    " + start + " -- " + end + "\n";
								exist = true;
						}
						if ( !exist){	//予約が１つも存在しない場合の処理
							res = "予約はありません";
						}
			} catch (Exception e) {
				e.printStackTrace();
			}

			//(3) MySQへの接続切断
			closeDB();

			return res;
		} catch(NumberFormatException e){
			res ="年月日には数字を指定してください";
			return res;
		}

		
		}

		/////詳細ボタンの機能メソッド　　MainFrameで使用
		public String getExplanation(String facility) {       
			String fac = "";
			connectDB();

			try {
					// 予約情報を取得するクエリ
					String sql = "SELECT explanation FROM db_reservation.facility WHERE facility_name='" + facility + "';";
					ResultSet rs = sqlStmt.executeQuery(sql);

					// 取得した情報を利用して何らかの処理を行うことができる

					// 例：取得した情報を文字列として結合する
					while (rs.next()) {
							String explanation = rs.getString("explanation");
							fac += explanation + "\n";
					}

					// データベースとの接続を解除する
					rs.close();
			} catch (Exception e) {
					e.printStackTrace();
			}finally {
				closeDB();
		}
			return fac;
	}

	////// ログイン・ログアウトボタンの処理
	public String loginLogout( MainFrame frame){
		String res="";	//結果を入れる変数
		reservation_userid = "";

		if ( flagLogin){	//ログアウトを行う処理
			flagLogin = false;
			frame.buttonLog.setLabel(" ログイン ");
		} else {			//ログインを行う処理
				//ログインダイアログの生成と表示
				LoginDialog ld = new LoginDialog(frame);
				ld.setVisible(true);
				ld.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

				//IDとパスワードの入力がキャンセルされたら，空文字列を結果として終了
				if ( ld.canceled){
					return "";
				}

				// ユーザIDとパスワードが入力された場合の処理
				// ユーザIDは他の機能のときに使用するのでメンバー変数に代入
				reservation_userid = ld.tfUserID.getText();
				// パスワードはここでしか使わないので，ローカル変数に代入
				String password = ld.tfPassword.getText();

				//(1) MySQLを使用する準備
				connectDB();

				//(2) MySQLの操作(SELECT文の実行)
				try {
							// userの情報を取得するクエリ
							String sql = "SELECT * FROM db_reservation.user WHERE user_id ='" + reservation_userid + "';";
							// クエリーを実行して結果セットを取得
							ResultSet rs = sqlStmt.executeQuery(sql);
							// 検索結果に対してパスワードチェック
							if (rs.next()){
								String password_from_db = rs.getString("password");
								if ( password_from_db.equals(password)){ //認証成功：データベースのIDとパスワードに一致
									flagLogin = true;
							frame.buttonLog.setLabel("ログアウト");
							res = "";
							
								} else {	//	認証失敗：パスワードが不一致
									res = "ログインできません．ID パスワードが　違います．";
								}
							} else {	//認証失敗；ユーザIDがデータベースに存在しない
								res = "ログインできません．ID パスワードが　違います．";
							}

				} catch (Exception e) {
					e.printStackTrace();
				}

				//(3) MySQへの接続切断
				closeDB();
			}

		return res;
	}
  	////ユーザIDを取得する
	public String getId() {
		return reservation_userid;
  }

		//// ユーザの予約状況確認
	public String getReservationOfUser(String user_id ){
		String res = "";
		if (flagLogin) { // ログインしていた場合
		connectDB();


			try {
					// ユーザの予約情報を取得するクエリ
					String sql = "SELECT * FROM reservation " +
															"WHERE user_id = '" + user_id + "' ";

					ResultSet rs = sqlStmt.executeQuery(sql);

					// 予約情報が存在するかチェック
					boolean exist = false;
							

					while (rs.next()) {
							// レコードから必要な情報を取得
							String date = rs.getString("reservation.date");
							String facility = rs.getString("reservation.facility_name");
							String startTime = rs.getString("reservation.start_time");
							String endTime = rs.getString("reservation.end_time");
							// 取得した情報を文字列として結合
							res += "予約日: " + date + " 施設: " + facility + " 開始時刻: " + startTime + " 終了時刻: " + endTime + "\n";
							exist = true;
							/////put delete method here
					}

					if (!exist) {
							res = "予約はありません";
					}
					
					rs.close();
					} catch (Exception e) {
							e.printStackTrace();
					} finally {
							closeDB();
					}		
		} else {
			res = "ログインしてください";
		}

		return res;
	}

	////// 新規予約の登録
	public String makeReservation(MainFrame frame){

		String res="";		//結果を入れる変数

		if ( flagLogin){ // ログインしていた場合
			//新規予約画面作成
			ReservationDialog rd = new ReservationDialog(frame);

			// 新規予約画面の予約日に，メイン画面に設定されている年月日を設定する
			rd.tfYear.setText(frame.tfYear.getText());
			rd.tfMonth.setText(frame.tfMonth.getText());
			rd.tfDay.setText(frame.tfDay.getText());

			// 新規予約画面を可視化
			rd.setVisible(true);
			if ( rd.canceled){
				return res;
			}
			try {
				//新規予約画面から年月日を取得
				String ryear_str = rd.tfYear.getText();
				String rmonth_str = rd.tfMonth.getText();
				String rday_str = rd.tfDay.getText();

				// 年月日が数字かどうかををチェックする処理
				int ryear = Integer.parseInt( ryear_str);
				int rmonth = Integer.parseInt( rmonth_str);
				int rday = Integer.parseInt( rday_str);

				System.out.println(ryear + "/" + rmonth + "/" + rday);

				if ( checkReservationDate( ryear, rmonth, rday)){	// 期間の条件を満たしている場合
					// 新規予約画面から施設名，開始時刻，終了時刻を取得
					String facility = rd.choiceFacility.getSelectedItem();
					String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
					String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

					if( st.equals(et)){		//開始時刻と終了時刻が等しい
						res = "開始時刻と終了時刻が同じです";
					} else {
						//(1) MySQLを使用する準備
						connectDB();


						try {
							// 月と日が一桁だったら，前に0をつける処理
							if (rmonth_str.length()==1) {
								rmonth_str = "0" + rmonth_str;
							}
							if ( rday_str.length()==1){
								rday_str = "0" + rday_str;
							}
							//(2) MySQLの操作(SELECT文の実行)
							String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
						      // 指定した施設の指定した予約日の予約情報を取得するクエリ
						      String sql = "SELECT * FROM db_reservation.reservation WHERE facility_name ='" + facility +
						      "' AND date = '" + rdate + "' ;";
						      // クエリーを実行して結果のセットを取得
						      ResultSet rs = sqlStmt.executeQuery(sql);
						      // 検索結果に対して重なりチェックの処理
						      boolean ng = false;	//重なりチェックの結果の初期値（重なっていない=false）を設定
							  // 取得したレコード一つ一つに対して確認
						      while(rs.next()){
							  		//レコードの開始時刻と終了時刻をそれぞれstartとendに設定
							        String start = rs.getString("start_time");
							        String end = rs.getString("end_time");

							        if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||		//レコードの開始時刻＜新規の開始時刻　AND　新規の開始時刻＜レコードの終了時刻
							        	 (st.compareTo(start)<0 && start.compareTo(et)<0)){		//新規の開始時刻＜レコードの開始時刻　AND　レコードの開始時刻＜新規の開始時刻
										 	// 重複有りの場合に ng をtrueに設定
							        	ng = true; break;
							        }
						      }
							  /// 重なりチェックの処理　ここまで  ///////

						      if (!ng){	//重なっていない場合
							  		//(2) MySQLの操作(INSERT文の実行)
						    	  sql = "INSERT INTO db_reservation.reservation (date,start_time,end_time,user_id,facility_name) VALUES ( '"
						    		  + rdate +"', '"  + st +"','" + et + "','" + reservation_userid +"','" + facility +"');";
						    	  int rs_int = sqlStmt.executeUpdate(sql);
						    	  res ="予約されました";
						      } else {	//重なっていた場合
						    	  res = "既にある予約に重なっています";
						      }
						}catch (Exception e) {
							e.printStackTrace();
						}
						//(3) MySQへの接続切断
						closeDB();
					}
				} else {
					res = "予約日が無効です．";
				}
			} catch(NumberFormatException e){
				res ="予約日には数字を指定してください";
			}
		} else { // ログインしていない場合
			res = "ログインしてください";
		}
		return res;
	}

	//// 予約日が2日後〜3ヶ月先の条件に入っているか確認するメソッド
	///　期間の条件を満たしていたら true 入っていなかったら false を返す
	private boolean checkReservationDate( int y, int m, int d){
		// 予約日
		Calendar dateR = Calendar.getInstance();
		dateR.set( y, m-1, d);	// 月から1引かなければならないことに注意！

		// 今日の１日後
		Calendar date1 = Calendar.getInstance();
		date1.add(Calendar.DATE, 1);

		// 今日の３ヶ月後（90日後)
		Calendar date2 = Calendar.getInstance();
		date2.add(Calendar.DATE, 90);

		if ( dateR.after(date1) && dateR.before(date2)){
			return true;
		}
		return false;
	}

	////予約キャンセル
	public String deleteReservation(MainFrame frame) {
    String res = "";

    if (!flagLogin) {
        res = "ログインしてください";
        return res;
    }

    CancelDialog cancelDialog = new CancelDialog(frame);
    // キャンセルダイアログの生成と表示
    cancelDialog.setVisible(true);
    cancelDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

    // ダイアログから入力された情報を取得
    if (cancelDialog.canceled) {
        return res;
    }

    try {
				// int reservationId = Integer.parseInt(reservation_userid);
        // 新規予約画面から年月日を取得
        String ryear_str = cancelDialog.tfYear.getText();
        String rmonth_str = cancelDialog.tfMonth.getText();
        String rday_str = cancelDialog.tfDay.getText();

				// 月と日が一桁だったら，前に0をつける処理
				if (rmonth_str.length()==1) {
					rmonth_str = "0" + rmonth_str;
				}
				if ( rday_str.length()==1){
					rday_str = "0" + rday_str;
				}

        String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

        String facility = cancelDialog.choiceFacility.getSelectedItem();
        String st = cancelDialog.startHour.getSelectedItem() + ":" + cancelDialog.startMinute.getSelectedItem() + ":00";

        // データベースに接続
        connectDB();

        // SQL文を作成して実行（PreparedStatementを使用してSQL文を実行）
        String deleteQuery = "DELETE FROM reservation WHERE date ="+ "'"+rdate +"'"+" AND start_time = "+ "'"+ st +"'"+" AND facility_name = "+ "'"+facility+"';";
        int rs_int = sqlStmt.executeUpdate(deleteQuery);

				res = rdate + ", " + facility + ", " + st + "の予約が取り消されました";

				// 接続をクローズ（仮のコード：接続を適切にクローズします）
				closeDB();
		} catch (SQLException e) {
			// 例外処理（仮のコード：エラーが発生した場合の処理を記述します）
			res = "予約の取り消し中にエラーが発生しました";
			e.printStackTrace();
		}
		return res;
	}     
}
	

		






