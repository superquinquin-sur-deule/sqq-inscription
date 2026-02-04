import { ref, type Ref } from 'vue'

export interface StepperOptions {
  initialValue?: number
  min?: number
  max?: number
  step?: number
}

export interface StepperReturn {
  value: Ref<number>
  increment: () => void
  decrement: () => void
  setValue: (newValue: number) => void
}

export function useStepper(options: StepperOptions = {}): StepperReturn {
  const { initialValue = 1, min = 0, max = Infinity, step = 1 } = options

  const value = ref(initialValue)

  const increment = (): void => {
    const v = Number(value.value)
    const next = Number.isFinite(v) ? Math.floor(v) + step : initialValue + step
    value.value = Math.min(next, max)
  }

  const decrement = (): void => {
    const v = Number(value.value)
    const next = Number.isFinite(v) ? Math.floor(v) - step : initialValue
    value.value = Math.max(next, min)
  }

  const setValue = (newValue: number): void => {
    value.value = Math.max(min, Math.min(newValue, max))
  }

  return { value, increment, decrement, setValue }
}
