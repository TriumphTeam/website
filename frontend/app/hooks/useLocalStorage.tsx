import {useEffect, useState} from "react"

function setValue(key: string, value: string) {
    try {
        window.localStorage.setItem(key, value)
    } catch (error) {
        console.log(error)
    }
}

function getValue(key: string): string | null {
    try {
        return window.localStorage.getItem(key)
    } catch (error) {
        console.log(error)
        return null
    }
}

export function useLocalStorage<T>(key: string, initialValue: string) {
    const [storedValue, setStoredValue] = useState(() => {
        const item = getValue(key)
        return item ?? initialValue
    })

    useEffect(() => {
        setValue(key, storedValue)
    }, [storedValue]);

    return [storedValue, setStoredValue] as const;
}
