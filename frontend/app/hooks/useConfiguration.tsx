import type {DocumentConfiguration} from "~/utils/Configurations"
import {useLocalStorage} from "~/hooks/useLocalStorage"

export type ConfigurationState = [DocumentConfiguration, DocumentConfiguration[], (key: string) => void]

export function useConfiguration(key: string, values: DocumentConfiguration[]): ConfigurationState {
    const first = values[0]
    const [storedKey, setStoredKey] = useLocalStorage(key, first.key)

    const current = values.find(value => value.key === storedKey) ?? first

    return [current, values, setStoredKey] as const
}
