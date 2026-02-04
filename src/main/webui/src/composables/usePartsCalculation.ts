import { computed, type ComputedRef, type Ref } from 'vue'

export interface PartsOption {
  checked: boolean
  parts: number
}

export interface PartsConfig {
  p100: PartsOption
  p10: PartsOption
  soutien: { checked: boolean; parts: number }
  binome: { enabled: boolean; parts: number }
}

export interface PartsCalculationReturn {
  totalParts: ComputedRef<number>
  totalAmount: ComputedRef<number>
}

export function usePartsCalculation(config: PartsConfig): PartsCalculationReturn {
  const totalParts = computed(() => {
    const p100parts = config.p100.checked ? config.p100.parts : 0
    const p10parts = config.p10.checked ? config.p10.parts : 0
    const binomeParts = config.binome.enabled ? config.binome.parts : 0
    const soutienParts = config.soutien.checked ? config.soutien.parts : 0
    return p100parts + p10parts + binomeParts + soutienParts
  })

  const totalAmount = computed(() => totalParts.value * 10)

  return { totalParts, totalAmount }
}

export function useSimplePartsCalculation(
  parts: Ref<number>,
  pricePerPart: number = 10
): { totalAmount: ComputedRef<number> } {
  const totalAmount = computed(() => parts.value * pricePerPart)
  return { totalAmount }
}
