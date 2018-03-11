package pl.com.tt.mock;

import java.lang.reflect.InvocationTargetException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class MockDemo {

	public static class Controller {

		public Integer process(String input) {
			// Do some really time consuming processing...
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// Shhh, we do not like being woken up
			}
			return 303;
		}
	}

	public static void main(String[] args)
			throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

		Controller dynamicType = new ByteBuddy().subclass(Controller.class).method(ElementMatchers.named("toString"))
				.intercept(FixedValue.value("Hello World!")).make().load(MockDemo.class.getClassLoader()).getLoaded()
				.getDeclaredConstructor().newInstance();

		System.out.println(dynamicType);

		// Mocking interface using Java Proxy

		// Mocking class using Byte Buddy
	}

}
