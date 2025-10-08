"use client"
import type {ReactNode} from "react";

export function Dropdown({small, children}: { small: boolean, children: ReactNode }) {
    return (
        <div className={`absolute w-36 top-0 ${small ? "mt-7" : "mt-9"} bg-dark-background-secondary rounded-md p-1`}>
            {children}
        </div>
    )
}

export function DropdownItem({text, onClick}: { text: string, onClick: () => void}) {
    return <div className="py-2 hover:bg-dark-background-primary" onClick={() => onClick()} key={`dropdown-${text}`}>{text}</div>
}
