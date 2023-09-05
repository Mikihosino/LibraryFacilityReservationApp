public class ReservationSystem {
//コントローラとmainFrameのクラスの名前を変更
	public static void main ( String argv[]){
		ReservationControl reservationControl = new ReservationControl();
		MainFrame mainFrame = new MainFrame(reservationControl);
		mainFrame.setBounds( 5,5,655,455);
		mainFrame.setVisible(true);
	}
}
