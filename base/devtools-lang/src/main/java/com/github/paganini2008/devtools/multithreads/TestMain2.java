package com.github.paganini2008.devtools.multithreads;

public class TestMain2 {

	public static void main(String[] args) throws Exception {
		SimpleThreadPool tp = new SimpleThreadPool(10, 1000L, Integer.MAX_VALUE);
		Promise<Long> p = tp.submit(getIt(1L));
		System.out.println("No. " + p.get());
		System.in.read();
		tp.shutdown();
		System.out.println("TestMain2.main()");
	}

	private static Action<Long> getIt(final long l) {
		return new Action<Long>() {
			public Long execute() throws Exception {
				return l;
			}

			public boolean shouldReact(Long result) {
				return result < 10;
			}

			public Long onReaction(Long result, ThreadPool threadPool) {
				ThreadUtils.randomSleep(1000L);
				return result + 1;
			}
		};
	}

}
