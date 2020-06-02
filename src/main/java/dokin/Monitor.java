package dokin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import lombok.SneakyThrows;
import lombok.val;

public class Monitor {

	private Timer timer;
	private Path dir;
	private Consumer<Path> calc;
	
	protected ExecutorService exec = Executors.newFixedThreadPool(10);
	
	public Monitor(Path dir, Consumer<Path> calc) {
		this.dir = dir;
		this.calc = calc;
	}
	
	
	protected void regist(Path file) {
		exec.execute(()->calc.accept(file));
	}

	/**
	 * タイマー処理
	 *  - ディレクトリを検索してファイルがあればハッシュ作成処理タスクに打ち込む
	 */
	@SneakyThrows
	protected void onTimer() {
		try(val stream = Files.list(dir)) {
			stream.filter(Files::isRegularFile).forEach(this::regist);
		}
	}
	
	/**
	 * Timerを3秒間隔で動かす
	 */
	public Monitor start() {
		if(this.timer == null) {
			this.timer = new Timer();
			this.timer.schedule(
				new TimerTask() { @Override public void run() { onTimer(); } }, 3000, 3000);
		}

		return this;
	}
	
	public void stop() {
		this.timer.cancel();
	}
	
}
