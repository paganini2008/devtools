package com.github.paganini2008.devtools.multithreads;

public class TestMain2 {

	public static void main(String[] args) throws Exception {
		AsyncThreadPool<Long> tp = ThreadUtils.newAsyncPool(ThreadUtils.newSimplePool(100));
		Promise<Long> p = tp.submitAndWait(getIt(6L));
		System.out.println("No. " + p.get());
		System.in.read();
		tp.shutdown();
		System.out.println("TestMain2.main()");
	}

	private static Execution<Long> getIt(long i) {
		return new Execution<Long>() {
			public Long execute() throws Exception {
				return i <= 1 ? 1 : i;
			}

			public void onSuccess(Long result, AsyncThreadPool<Long> threadPool) {
				if (i > 1) {
					Promise<Long> p = threadPool.submitAndWait(getIt(result - 1));
					Promise<Long> p2 = threadPool.submitAndWait(getIt(result - 2));
					System.out.println(ThreadUtils.currentThreadName() + ", Fab2222 : " + (p.get() + p2.get()));
				}
			}
		};
	}

}
