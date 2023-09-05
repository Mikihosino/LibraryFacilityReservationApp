import java.awt.*;
import java.awt.event.*;
import java.awt.Button;

public class CancelDialog extends Dialog implements ActionListener, WindowListener, ItemListener{

  boolean canceled;		//キャンセルされたら true 予約実行ボタンのときは false
	Panel panelNorth;		//上部パネル
	Panel panelMid;			//中央のパネル
	Panel panelSouth;		//下部パネル

	ChoiceFacility choiceFacility;
	TextField tfYear,tfMonth,tfDay;	// 年月日のテキストフィールド
	ChoiceHour startHour;			// 予約開始時刻　時の選択用ボックス
	ChoiceMinute startMinute;		// 予約開始時刻　分の選択用ボックス

	// ボタン
	Button buttonOK;
	Button buttonCancel;

	ReservationControl reservationControl;

  public CancelDialog(Frame owner) {
    // Basic class(Dialog) constructor call
    super(owner, "削除する施設予約の施設と予約日と始まる予約時間を記入してください.", true);
    setSize(400, 300);

    canceled = true;

		//施設選択ボックスの生成
		choiceFacility = new ChoiceFacility();
		//テキストフィールドの生成　年月日
		tfYear = new TextField("",4);
		tfMonth = new TextField("",2);
		tfDay = new TextField("",2);
		//開始時刻　時分選択ボックスの生成
		startHour = new ChoiceHour();
		startMinute = new ChoiceMinute();

		//ボタンの生成
		buttonOK = new Button("取消実行");
		buttonCancel = new Button("キャンセル");

    // パネルの生成
    panelNorth = new Panel();
    panelMid = new Panel();
    panelSouth = new Panel();

		//上部パネルに施設選択ボックス，年月日入力欄を追加
		panelNorth.add( new Label("施設　"));
		panelNorth.add(choiceFacility);
		panelNorth.add( new Label("予約日 "));
		panelNorth.add(tfYear);
		panelNorth.add(new Label("年"));
		panelNorth.add(tfMonth);
		panelNorth.add(new Label("月"));
		panelNorth.add(tfDay);
		panelNorth.add(new Label("日　"));

		//中央パネルに予約　開始時刻，終了時刻入力用選択ボックスを追加
		panelMid.add( new Label("予約時間　"));
		panelMid.add( startHour);
		panelMid.add( new Label("時"));
		panelMid.add( startMinute);
		panelMid.add( new Label("分　〜　"));
    
    // 下部パネルに2つのボタンを追加
    panelSouth.add(buttonCancel);
    panelSouth.add(buttonOK);

		// ReservationDialogをBorderLayoutに設定し，3つのパネルを追加
		setLayout(new BorderLayout());
		add(panelNorth, BorderLayout.NORTH);
		add(panelMid,BorderLayout.CENTER);
		add(panelSouth, BorderLayout.SOUTH);

   // ウィンドウリスナを追加
		addWindowListener(this);
		// ボタンにアクションリスナを追加
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
		//施設選択ボックス，時・分選択ボックスそれぞれに項目リスナを追加
		choiceFacility.addItemListener(this);
		startHour.addItemListener(this);
		startMinute.addItemListener(this);

		// 選択されている施設によって，時刻の範囲を設定する．
		resetTimeRange();

    // 大きさの設定，ウィンドウのサイズ変更不可の設定
    this.setBounds(100, 100, 700, 180);
    setResizable(false);
}

	private void resetTimeRange() {
		// 選択されている施設によって，時刻の範囲を設定する．
		if ( choiceFacility.getSelectedIndex()==0){
			// 最初の施設(小ホールのとき)の設定
			startHour.resetRange(10, 20);
		} else {
			// 小ホール以外の設定
			startHour.resetRange(9, 19);
		}
	}


  @Override
	public void actionPerformed(ActionEvent arg0) {
		if ( arg0.getSource() == buttonCancel){
			canceled = true; // キャンセルボタンが押されたら true にする
			setVisible(false);
			dispose();
		} else if ( arg0.getSource() == buttonOK){
			canceled = false;
		}
		setVisible(false);
		dispose();
	}

  @Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		setVisible(false);
		dispose();
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
	public void itemStateChanged(ItemEvent arg0) {
		if ( arg0.getSource()==choiceFacility){
			// 施設が変更されたら，施設に応じた範囲を設定
			resetTimeRange();
		}
	}
}
