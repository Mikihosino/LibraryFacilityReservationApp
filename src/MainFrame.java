
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends Frame implements ActionListener,WindowListener,KeyListener{

	ReservationControl reservationControl;
	boolean loginStatus;

	Panel panelNorth;		//上部パネル
	Panel panelNorthSub1;		//上部パネルの上
	Panel panelNorthSub2;		//上部パネルの中央
	Panel panelNorthSub3;		//上部パネルの下
	Panel panelMid;		//中央パネル
	Panel panelSouth;		//下部パネル

	Button buttonLog;			// ログイン　・　ログアウト　ボタン
	Button buttonExplanation;		// 施設概要　説明ボタン
	Button buttonVacancy;			//  空き状況確認
	Button buttonReservation;		// 新規予約ボタン
	Button buttonConfirm;			// 予約の確認
	Button buttonCancel;			// 予約のキャンセルボタン

	ChoiceFacility choiceFacility;		// 施設選択用選択ボックス
	TextField tfYear,tfMonth,tfDay;		// 年月日のテキストフィールド
	TextArea textMessage;			// 結果表示用メッセージ欄

	public MainFrame( ReservationControl rc){

		reservationControl = rc;


		// ボタンの生成
		buttonLog = new Button(" ログイン ");
		buttonExplanation = new Button("施設概要");
		buttonVacancy = new Button("空き状況確認");
		buttonReservation = new Button("新規予約");
		buttonConfirm = new Button("予約の確認");
		buttonCancel = new Button("予約のキャンセル");

		// 設備チョイスボックスの生成
		choiceFacility = new ChoiceFacility();
		tfYear = new TextField("",4);
		tfMonth = new TextField("",2);
		tfDay = new TextField("",2);

		// 上中下のパネルを使うため，レイアウトマネージャーにBorderLayoutを設定
		setLayout( new BorderLayout());


		// 上部パネルの上パネルに　　予約システム　というラベルと　[ログイン]ボタンを追加
		panelNorthSub1 = new Panel();
		panelNorthSub1.add(new Label("施設予約システム　　　　　　　　　　　　　　　　"));
		panelNorthSub1.add(buttonLog);

		// 上部パネルの中央パネルに　　　施設　　[施設名選択]チョイス   [概要説明]ボタンを追加
		panelNorthSub2 = new Panel();
		panelNorthSub2.add(new Label("施設　　"));
		panelNorthSub2.add( choiceFacility);
		panelNorthSub2.add(new Label("　　　"));
		panelNorthSub2.add( buttonExplanation);  //詳細ボタン

		// 上部パネルのしたパネルに年月日入力欄と　空き状況確認ボタンを追加
		panelNorthSub3 = new Panel();
		panelNorthSub3.add(new Label("　　"));
		panelNorthSub3.add(tfYear);
		panelNorthSub3.add(new Label("年"));
		panelNorthSub3.add(tfMonth);
		panelNorthSub3.add(new Label("月"));
		panelNorthSub3.add(tfDay);
		panelNorthSub3.add(new Label("日　"));
		panelNorthSub3.add( buttonVacancy);


		// 上部パネルに３つのパネルを追加
		panelNorth = new Panel(new BorderLayout());
		panelNorth.add(panelNorthSub1, BorderLayout.NORTH);
		panelNorth.add(panelNorthSub2, BorderLayout.CENTER);
		panelNorth.add(panelNorthSub3, BorderLayout.SOUTH);
		// メイン画面(MainFrame)に上部パネルを追加
		add(panelNorth,BorderLayout.NORTH);


		// 中央パネルにテキストメッセージ欄を設定
		panelMid = new Panel();
		textMessage = new TextArea( 20, 80);
		textMessage.setEditable(false);
		panelMid.add(textMessage);
		// メイン画面(MainFrame)に中央パネルを追加
		add(panelMid,BorderLayout.CENTER);


		// 下部パネルにボタンを設定
		panelSouth = new Panel();
		panelSouth.add(buttonReservation);
		panelSouth.add(new Label("　　　"));
		panelSouth.add(buttonConfirm);
		panelSouth.add(new Label("　　　"));
		panelSouth.add(buttonCancel);
		// メイン画面(MainFrame)に下部パネルを追加
		add( panelSouth,BorderLayout.SOUTH);

		//ボタンのアクションリスナの追加
		buttonLog.addActionListener(this);
		buttonExplanation.addActionListener(this);
		buttonVacancy.addActionListener(this);
		buttonReservation.addActionListener(this);
		buttonConfirm.addActionListener(this);
		buttonCancel.addActionListener(this);

		addWindowListener(this);
		addKeyListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String result = new String();
    String explanation = new String();
		String confirm = new String();

		textMessage.setText("");
		if ( arg0.getSource() == buttonVacancy){ //// 空き状況確認ボタン
			result = reservationControl.getReservationOn( choiceFacility.getSelectedItem(),
							tfYear.getText(), tfMonth.getText(), tfDay.getText());
		}
    if ( arg0.getSource() == buttonExplanation){ 																								//// 詳細確認ボタン機能を追加
			explanation = reservationControl.getExplanation( choiceFacility.getSelectedItem());
		}
		
		else if (arg0.getSource() == buttonLog){     ////login
			result = reservationControl.loginLogout(this);
		}

		else if (arg0.getSource() == buttonConfirm ){  ////予約確認
			loginStatus= reservationControl.getFlagLogin();
			confirm = reservationControl.getReservationOfUser(reservationControl.getId()); 
		}

		else if ( arg0.getSource() == buttonReservation){	/// 新規予約ボタン
			result = reservationControl.makeReservation(this);
		}

		else if ( arg0.getSource() == buttonCancel){ ////取消
			result = reservationControl.deleteReservation(this);
		}

		textMessage.setText(result + explanation + confirm);
	}
	

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {

	}

	@Override
	public void keyPressed(KeyEvent arg0) {

	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

}