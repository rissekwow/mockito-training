package pl.com.tt.mocking;

import java.util.Arrays;
import java.util.HashSet;

import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import pl.com.tt.mocking.Engine.EngineError;

public class CarTest implements WithAssertions, WithBDDMockito {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Engine engine;

	@Mock
	private FuelPump fuelPump;

	@InjectMocks
	private Car car;

	@Test
	public void shouldBeReadyToStart() {
		when(fuelPump.isFuelDetected()).thenReturn(true);
		when(fuelPump.isOn()).thenReturn(true);
		when(fuelPump.isSystemError()).thenReturn(false);
		when(fuelPump.isRunning()).thenReturn(false);
		when(engine.isPowerOn()).thenReturn(true);
		when(engine.isRunning()).thenReturn(false);
		when(engine.getErrors()).thenReturn(new HashSet<EngineError>());
		assertThat(car.checkBeforeStart(1L)).isEqualTo(true);
	}

	@Test
	public void shouldNotBeReadyToStartWhenEngineErrors() {
		when(fuelPump.isFuelDetected()).thenReturn(true);
		when(fuelPump.isOn()).thenReturn(true);
		when(fuelPump.isSystemError()).thenReturn(false);
		when(fuelPump.isRunning()).thenReturn(false);
		when(engine.isPowerOn()).thenReturn(true);
		when(engine.isRunning()).thenReturn(false);
		when(engine.getErrors()).thenReturn(new HashSet<EngineError>(Arrays.asList(EngineError.INJECTOR_ERROR)));
		assertThat(car.checkBeforeStart(1L)).isEqualTo(false);
	}

	@Test
	public void shouldNotBeReadyToNoFuelDetected() {
		when(fuelPump.isFuelDetected()).thenReturn(false);
		when(fuelPump.isOn()).thenReturn(true);
		when(fuelPump.isSystemError()).thenReturn(false);
		when(fuelPump.isRunning()).thenReturn(false);
		when(engine.isPowerOn()).thenReturn(true);
		when(engine.isRunning()).thenReturn(false);
		when(engine.getErrors()).thenReturn(new HashSet<EngineError>());
		assertThat(car.checkBeforeStart(1L)).isEqualTo(false);
	}

	@Test
	public void shouldStartWhenAllComponentsOk() {
		car = spy(car);
		// when(car.checkBeforeStart(anyLong())).thenReturn(true); -- this line is not
		// working when spy is used
		doReturn(true).when(car).checkBeforeStart(anyLong());
		car.start(1l);
		verify(fuelPump, times(1)).start();
		verify(engine, times(1)).start();
	}

	@Test
	public void shouldNotStartWhenSomethingIsWrong() {
		car = spy(car);
		// when(car.checkBeforeStart(anyLong())).thenReturn(true); -- this line is not
		// working when spy is used
		doReturn(false).when(car).checkBeforeStart(anyLong());
		car.start(1l);
		verify(fuelPump, times(0)).start();
		verify(engine, times(0)).start();
	}

	@Test
	public void shouldPassParamToCheckBeforeStart() {
		car = spy(car);
		ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
		doReturn(false).when(car).checkBeforeStart(argumentCaptor.capture());
		car.start(1l);
		assertThat(argumentCaptor.getValue()).isEqualTo(1L);

	}

	@Test
	public void checkShouldPassWhenOilTemperatureIsBelow100() {
		when(fuelPump.isRunning()).thenReturn(true);
		when(engine.isRunning()).thenReturn(true);
		when(fuelPump.isSystemError()).thenReturn(false);
		when(engine.getOilTemperature().longValue()).thenReturn(80L);
		when(engine.getOilPressure().longValue()).thenReturn(600L);
		assertThat(car.check()).isTrue();
	}

	@Test
	public void checkShouldFailWhenOilTemperatureIsAbove100() {
		when(fuelPump.isRunning()).thenReturn(false);
		when(engine.isRunning()).thenReturn(true);
		when(engine.getOilTemperature().longValue()).thenReturn(110L);
		when(engine.getOilPressure().longValue()).thenReturn(600L);
		assertThat(car.check()).isFalse();
	}

	@Test
	public void checkShouldFailWhenOilPressureIsAbove1000() {
		when(fuelPump.isRunning()).thenReturn(false);
		when(engine.isRunning()).thenReturn(true);
		when(engine.getOilTemperature().longValue()).thenReturn(80L);
		when(engine.getOilPressure().longValue()).thenReturn(1200L);
		assertThat(car.check()).isFalse();
	}
}
