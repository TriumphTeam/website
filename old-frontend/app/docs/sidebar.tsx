"use client"
import {Link, NavLink, useParams} from "react-router"
import {useOpenable} from "~/hooks/useOpenable"
import {Dropdown, DropdownItem} from "~/docs/dropdown"
import {
    ContentSection,
    type NavigationPage,
    type Nullable,
    type ProjectVersion,
    type VersionData,
    type VersionDocument,
} from "@lichthund/triumph-docs-serializable"
import React, {useState} from "react"
import {AnimatePresence, motion} from "motion/react"
import useSWR from "swr"
import Fuse from "fuse.js"

const fuseOptions = {
    threshold: 0.4,
    keys: ["content"],
}

export function Sidebar({project, data, name, document}: {
    project: string,
    data: ProjectVersion,
    name: string,
    document: VersionDocument
}) {
    const [open, setOpen] = useState(false)

    function ControlButton() {
        return <div className="xl:hidden fixed z-[100] top-0 text-2xl pl-6 py-8">
            <motion.div
                initial={{rotate: "0deg", scale: 0.5}}
                animate={{rotate: "180deg", scale: 1}}
                onClick={() => setOpen(!open)}
            >
                {open ? <i className="fa-solid fa-xmark"/> : <i className="fa-solid fa-bars"/>}
            </motion.div>
        </div>
    }

    return <>
        <ControlButton/>
        <div className="xl:hidden w-6"/>
        <motion.div
            initial={{x: "-100%"}}
            animate={{x: open ? "0%" : "-100%"}}
            transition={{
                type: "spring",
                stiffness: 500,
                damping: 25,
                duration: 0.1,
            }}
            className={`
                fixed w-screen z-50 h-screen flex
                md:w-72
                xl:sticky xl:top-0 xl:min-w-60 xl:w-60 xl:flex xl:!transform-none
                2xl:min-w-72 2xl:w-72
                flex-col gap-4 px-4 justify-center
                bg-dark-background noise
                border-r-2 border-dark-surface
            `}>
            <ProjectHeader
                key="project-header"
                project={project}
                projectName={name}
                versions={document.versions}
            />
            <ProjectButtons
                key="project-buttons"
                discord={document.discord}
                github={document.github}
                javadocs={document.javadocs}
            />
            <SearchBar key="search-bar" version={data.version}/>
            <NavigationArea
                key="navigation-area"
                document={document}
            />
            <SmallFooter key="small-footer"/>
        </motion.div>
    </>
}

function ProjectHeader({project, projectName, versions}: {
    project: string,
    projectName: string,
    versions: Array<VersionData>
}) {
    return (
        <div className="grid grid-cols-1 w-full justify-items-center gap-4 pt-6 select-none">
            <div className="flex items-center">
                <Link to="/" relative="path">
                    <img className="self-center h-16"
                         src={`http://localhost:8001/assets/${project}/icon.png`} alt="Logo"/>
                </Link>
            </div>
            <div className="flex items-center gap-2">
                <div className="col-span-1 text-center font-bold text-lg uppercase">
                    <h1>{projectName}</h1>
                </div>
                <VersionComponent key="version-component" project={project} versions={versions}/>
            </div>
        </div>
    )
}

function VersionComponent({project, versions}: { project: string, versions: Array<VersionData> }) {
    const [open, toggleOpen, ref] = useOpenable()

    const isSingular = versions.length <= 1
    const cursor = isSingular ? "default" : "pointer"
    const current = versions.find(version => version.current)

    if (!current) return <></>

    return (
        <div
            className={`relative flex items-center justify-center rounded-sm bg-(--project-color) px-2 text-center text-md cursor-${cursor}`}
            ref={ref} onClick={toggleOpen}>
            {current.reference}
            <AnimatePresence initial={false}>
                {
                    (open && versions.length > 1) && (
                        <Dropdown key="version-dropdown" small={true}>
                            {
                                versions.map((version) => <DropdownItem
                                    key={`version-dropdown-${version}`}
                                    text={version.reference}
                                    destination={`docs/${version.reference}/${project}/introduction`}
                                />)
                            }
                        </Dropdown>
                    )
                }
            </AnimatePresence>
        </div>
    )
}

function ProjectButtons({discord, github, javadocs}: {
    github: Nullable<string>,
    javadocs: Nullable<string>,
    discord: Nullable<string>,
}) {
    return (
        <div className="flex justify-center items-center gap-2 px-4">
            <ProjectButton key="project-button-discord" tooltip="Discord" icon="fa-brands fa-discord" link={discord}/>
            <ProjectButton key="project-button-github" tooltip="Github" icon="fa-brands fa-github" link={github}/>
            <ProjectButton key="project-button-javadocs" tooltip="Javadocs" icon="fa-solid fa-book" link={javadocs}/>
        </div>
    )
}

function ProjectButton({tooltip, icon, link}: { tooltip: string, icon: string, link: Nullable<string> }) {

    const isEnabled = link != null

    const className = `w-1/3 flex justify-center items-center bg-dark-surface rounded-md p-2`

    if (!isEnabled) return (
        <div className={`${className} text-white/10`}>
            <i className={icon}/>
        </div>
    )

    return (
        <a
            href={link ? link : ""}
            target="_blank"
            rel="noopener noreferrer"
            data-tooltip={tooltip}
            className={`${className} transition duration-300 ease-in-out hover:bg-(--project-color)`}
        >
            <i className={icon}/>
        </a>
    )
}

function SearchBar({version}: { version: number }) {

    const [open, toggleOpen, ref] = useOpenable()

    return <>
        <div
            className="flex items-center w-full mx-auto bg-dark-surface rounded-lg h-12 cursor-pointer"
            onClick={toggleOpen}
        >
            <div className="w-full">
        <span
            className="w-full px-4 py-1 rounded-full focus:outline-none pointer-events-none text-white/50 select-none">
          Search
        </span>
            </div>
            <div>
                <div className="flex items-center justify-center w-12 h-12 text-white/35 rounded-r-lg">
                    <i className="w-5 h-5 fa-solid fa-magnifying-glass"/>
                </div>
            </div>
        </div>
        <AnimatePresence initial={false}>
            {
                open && <SearchArea reference={ref} click={toggleOpen} version={version}/>
            }
        </AnimatePresence>
    </>
}

function SearchArea({reference, version, click}: {
    reference: React.RefObject<HTMLDivElement | null>,
    version: number,
    click: () => void
}) {
    return <motion.div
        initial={{opacity: 0}}
        animate={{opacity: 1}}
        exit={{opacity: 0}}
        className="fixed w-screen h-screen top-0 left-0 bg-black/60 backdrop-blur-sm z-100 flex justify-center items-center"
    >
        <motion.div
            initial={{y: "100%"}}
            animate={{y: "0%"}}
            transition={{
                type: "spring",
                stiffness: 500,
                damping: 25,
            }}
            ref={reference}
            className="w-3/4 md:w-[725px]"
        >
            <SearchDataArea version={version} click={click}/>
        </motion.div>
    </motion.div>
}

function SearchDataArea({version, click}: { version: number, click: () => void }) {
    const {data, error} = useSWR<ContentSection[]>(`/search-data?version=${version}`)

    if (error || !data) return <></>

    const fuse = new Fuse(data, fuseOptions)

    return <Search key="search-bar-search" fuse={fuse} click={click}/>
}

function Search({fuse, click}: { fuse: Fuse<ContentSection>, click: () => void }) {
    const [searchQuery, setSearchQuery] = useState("")

    const results = fuse.search(searchQuery)
    const words = searchQuery.split(" ")

    // This is horrible to look at.
    function MatchContent({content}: { content: string }) {
        const contentWords = content.split(" ")
        let firstMatchIndex = -1

        // Find the first matching word
        for (let i = 0; i < contentWords.length; i++) {
            for (const searchWord of words) {
                if (contentWords[i].toLowerCase().includes(searchWord.toLowerCase())) {
                    firstMatchIndex = i
                    break
                }
            }
            if (firstMatchIndex !== -1) break
        }

        if (firstMatchIndex === -1) return <>{content}</>

        // Get 2 words before and 2 words after the match.
        const start = Math.max(0, firstMatchIndex - 5)
        const end = Math.min(contentWords.length, firstMatchIndex + 6)
        const relevantWords = contentWords.slice(start, end)

        const parts = relevantWords.map((word, index) => {
            let matchFound = false
            let matchStart = -1
            let matchLength = 0

            for (const searchWord of words) {
                const wordIndex = word.toLowerCase().indexOf(searchWord.toLowerCase())
                if (wordIndex !== -1 && searchWord.length > matchLength) {
                    matchFound = true
                    matchStart = wordIndex
                    matchLength = searchWord.length
                }
            }

            if (matchFound) {
                return (
                    <>
                        {word.substring(0, matchStart)}
                        <span className="text-white">
                            {word.substring(matchStart, matchStart + matchLength)}
                        </span>
                        {word.substring(matchStart + matchLength)}
                        {" "}
                    </>
                )
            }

            return word + " "
        })

        return <>{start > 0 ? "... " : ""}{parts}{end < contentWords.length ? "..." : ""}</>
    }

    function Results() {
        if (results.length === 0) return <div
            className="w-full h-full flex justify-center items-center text-white/50 text-center">
            <div>No results found</div>
        </div>

        return <>
            {
                map(
                    groupBy(
                        results
                            .map((result) => result.item),
                        (item) => item.pageId,
                    ),
                    (key, value) => {

                        let first: ContentSection
                        if (value.length > 0) {
                            first = value[0]
                        } else {
                            return <></>
                        }

                        return <div>
                            <div key={`search-${key}`} className="font-bold py-2">{first.title}</div>
                            <div className="flex flex-col gap-2">
                                {
                                    value.map((item) => {
                                        return <Link to={`../${item.pageId}#${item.sectionId}`} relative="path"
                                                     onClick={click}>
                                            <div
                                                key={item.pageId + item.sectionId}
                                                className="bg-dark-surface hover:bg-dark-surface-hover rounded-lg flex flex-row"
                                            >
                                                <div className="px-4 flex justify-center items-center"><i
                                                    className="fa-solid fa-hashtag"/></div>
                                                <div className="flex flex-col py-2">
                                                    <div className="text-white/50 text-sm py-1">{item.section}</div>
                                                    <div className="text-white/50 py-1"><MatchContent
                                                        content={item.content}/></div>
                                                </div>
                                            </div>
                                        </Link>
                                    })
                                }
                            </div>
                        </div>
                    })
            }
        </>
    }

    return <div className="flex flex-col gap-2 p-4 w-full rounded-lg bg-dark-background">
        <div className="flex justify-between items-center bg-dark-surface w-full p-4 rounded-lg">
            <input
                autoFocus={true}
                autoCorrect="off"
                autoComplete="off"
                className="text-white text-lg w-full outline-none"
                type="text"
                placeholder="Search"
                maxLength={64}
                value={searchQuery}
                onChange={(e) => {
                    setSearchQuery(e.target.value)
                }}
            />
            <i className="fa-solid fa-xmark text-white/50 hover:text-white cursor-pointer" onClick={() => {
                setSearchQuery("")
            }}/>
        </div>
        <div className="w-full h-[625px] overflow-auto p-4 rounded-lg">
            <Results/>
        </div>
    </div>
}

function NavigationArea({document}: { document: VersionDocument }) {
    return (
        <div className="px-4 overflow-y-auto overflow-x-hidden overscroll-contain grow">
            <div className="grid grid-cols-1 gap-10">
                {
                    document.groups.map((group) => <NavigationGroupArea
                        key={`navigation-group-${group.name}`}
                        text={group.name}
                        pages={group.pages}
                    />)
                }
            </div>
        </div>
    )
}

function NavigationGroupArea({text, pages}: { text: string, pages: NavigationPage[], }) {
    return (
        <div>
            <h1 className="text-white xl:text-lg 2xl:text-xl font-bold">{text}</h1>
            {
                pages.map((page, _) => <NavigationPageArea
                    key={`navigation-page-${page.id}`}
                    text={page.name}
                    link={page.id}
                />)
            }
        </div>
    )
}

function NavigationPageArea({text, link}: { text: string, link: string, }) {
    const {page} = useParams()

    const color = page === link ? "text-(--project-color)" : ""

    return (
        <div className={`pt-2 ${color}`}>
            <NavLink
                to={`../${link}`} relative="path"
                className="xl:text-base 2xl:text-lg hover:text-(--project-color) transition ease-in-out"
            >
                {text}
            </NavLink>
        </div>
    )
}

function SmallFooter() {

    const year = new Date().getFullYear()

    return (
        <div className="flex-none text-[0.5em] text-center py-2">
            Copyright © 2020-{year}, TriumphTeam. All Rights Reserved.
        </div>
    )
}

function groupBy<T, K>(items: T[], keySelector: (i: T) => K): Map<K, T[]> {
    const destination = new Map<K, T[]>()

    for (const item of items) {
        const key = keySelector(item)
        const list = destination.get(key) ?? []
        list.push(item)
        destination.set(key, list)
    }

    return destination
}

function map<T, K, V>(map: Map<K, T>, transform: (key: K, value: T) => V): V[] {
    const entries = map.entries()
    const result: V[] = []

    for (const [key, value] of entries) {
        result.push(transform(key, value))
    }

    return result
}
