import { ref, type Ref } from 'vue'

export interface ModalStateReturn<T> {
  data: Ref<T | null>
  open: (payload: T) => void
  close: () => void
}

export function useModalState<T = unknown>(): ModalStateReturn<T> {
  const data = ref<T | null>(null) as Ref<T | null>

  const open = (payload: T): void => {
    data.value = payload
  }

  const close = (): void => {
    data.value = null
  }

  return { data, open, close }
}
