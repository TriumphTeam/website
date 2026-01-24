"use client"
import {motion} from "motion/react"
import type {ReactNode} from "react"
import {Link} from "react-router"

export function Dropdown({small, children}: { small: boolean, children: ReactNode }) {
    return (
        <motion.div
            initial={{opacity: 0, scale: 0}}
            animate={{opacity: 1, scale: 1}}
            exit={{opacity: 0, scale: 0}}
            className={`absolute w-36 z-65 top-0 ${small ? "mt-7" : "mt-9"} bg-dark-surface rounded-md p-1`}
        >
            {children}
        </motion.div>
    )
}

export function DropdownItem({text, destination}: { text: string, destination: string}) {
    return <Link to={`../${destination}`} relative="route">
        <div className="py-2 hover:bg-dark-background" key={`dropdown-${text}`}>{text}</div>
    </Link>
}
