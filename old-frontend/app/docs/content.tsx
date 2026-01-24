"use client"
import {Link, useParams} from "react-router"
import type {ConfigurationState} from "~/hooks/useConfiguration"
import useSWR from "swr"
import {type PageContent, type PageDocument} from "@lichthund/triumph-docs-serializable"
import "./one_dark.css"
import {PageDocumentComponent, Separator} from "~/docs/doc-component/pageDocument"
import React, {useEffect, useState} from "react"
import {motion} from "motion/react"
import {ASSETS_URL} from "~/axios/Api"

export function Content(
    {
        version,
        page,
        buildToolState,
        languageState,
        platformState,
    }: {
        version: number,
        page: string,
        buildToolState: ConfigurationState,
        languageState: ConfigurationState,
        platformState: ConfigurationState,
    },
) {
    const {data, error} = useSWR<PageDocument>(`/page?version=${version}&page=${page}`)

    if (error || !data) return <></>

    return <PageContents
        key="page-content"
        pageDocument={data}
        buildToolState={buildToolState}
        languageState={languageState}
        platformState={platformState}
    />
}

function PageContents({pageDocument, buildToolState, languageState, platformState}: {
    pageDocument: PageDocument,
    buildToolState: ConfigurationState,
    languageState: ConfigurationState,
    platformState: ConfigurationState,
}) {

    const previous = pageDocument.previous
    const next = pageDocument.next

    const banner = pageDocument.banner

    // Handle scrolling to hash on mount and hash changes
    useEffect(() => {
        const scrollToHash = () => {
            const hash = window.location.hash.slice(1) // Remove the '#' character
            if (hash) {
                // Small delay to ensure content is rendered
                setTimeout(() => {
                    const element = document.getElementById(hash)
                    if (element) {
                        element.scrollIntoView({behavior: "smooth", block: "start"})
                    }
                }, 100)
            }
        }

        // Scroll on mount if hash exists
        scrollToHash()

        // Listen for hash changes
        window.addEventListener("hashchange", (e) => {
            if (e.oldURL === e.newURL) return
            console.log(`hash changed ${e.oldURL} -> ${e.newURL}`)
            scrollToHash()
        })

        return () => {
            window.removeEventListener("hashchange", scrollToHash)
        }
    }, [pageDocument])

    return <>
        <div className="flex-1 flex flex-col min-h-0 min-w-0 px-6 items-center">
            <div id="doc-content" className="flex-1 [&>*]:px-2 pt-12 w-full md:w-8/10">
                <h1 className="text-4xl font-medium text-white text-center pointer-events-none">{pageDocument.name}</h1>
                <h2 className="text-lg text-center">{pageDocument.description}</h2>
                <Separator/>
                {
                    banner && <div className="flex justify-center items-center">
                        <img className="w-4/5" src={`${ASSETS_URL}${banner}`} alt="page-banner"/>
                    </div>
                }
                <TestGui rows={6}/>
                <PageDocumentComponent
                    document={pageDocument}
                    buildToolState={buildToolState}
                    languageState={languageState}
                    platformState={platformState}
                />
            </div>
            <div className="px-2 mt-12 pb-8 flex items-center justify-between gap-2 text-sm w-full md:w-8/10">
                <div>
                    {
                        previous && <Link to={`../${previous.id}`} relative="path"
                                          className="flex items-center gap-2 hover:text-(--project-color) transition ease-in-out">
                            <i className="fa-solid fa-angle-left"/>
                            <span>{previous.name}</span>
                        </Link>
                    }
                </div>
                <div>
                    {
                        next != null && <Link to={`../${next.id}`} relative="path"
                                              className="flex items-center gap-2 hover:text-(--project-color) transition ease-in-out">
                            <span>{next.name}</span>
                            <i className="fa-solid fa-angle-right"/>
                        </Link>
                    }
                </div>
            </div>
        </div>
        <ContentSidebar pageDocument={pageDocument} buildToolState={buildToolState} languageState={languageState}
                        platformState={platformState}/>
    </>
}

function TestGui({rows}: { rows: number }) {
    return <div style={{imageRendering: "pixelated"}}>
        <div
            className="w-[352px] h-[264px] bg-cover bg-[url(/assets/generic_chest_top.png)] p-[5px] font-[Monocraft] select-none"
        >
            <div className="h-full">
                <div className="px-[10px] w-full text-lg">Title goes here</div>
                <div className="w-full h-full flex flex-col gap-1.5 pt-[2px] px-[10px] pb-[40px]">
                    {
                        Array.from({length: rows}).map((_, index) => {
                            return <div className="flex flex-row gap-1 flex-1">
                                {
                                    Array.from({length: 9}).map((_, index) => {
                                        return <McSlot/>
                                    })
                                }
                            </div>
                        })
                    }
                </div>
            </div>
        </div>
    </div>
}

function McSlot() {

    return <>
        <div
            className="flex-1 hover:bg-white/20 flex justify-center items-center w-full h-full hover:[&_#tooltip]:block"
        >
            <McTooltip text="Bitch"/>
        </div>
    </>
}

function McTooltip({text}: { text: string }) {
    return (
        <div
            id="tooltip"
            className="hidden fixed ml-32 mb-16 p-4 z-50 bg-violet-950 pointer-events-none"
        >
            {text}
        </div>
    )
}

function ContentSidebar({pageDocument, buildToolState, languageState, platformState}: {
    pageDocument: PageDocument,
    buildToolState: ConfigurationState,
    languageState: ConfigurationState,
    platformState: ConfigurationState,
}) {

    const [open, setOpen] = useState(false)

    function ControlButton() {
        return <div className="lg:hidden fixed z-[49] top-0 right-0 text-2xl pr-6 py-8">
            <motion.div
                initial={{rotate: "0deg", scale: 0.5}}
                animate={{rotate: "180deg", scale: 1}}
                onClick={() => setOpen(!open)}
            >
                {open ? <i className="fa-solid fa-xmark"/> : <i className="fa-solid fa-gear"/>}
            </motion.div>
        </div>
    }

    return <>
        <ControlButton/>
        <div className="lg:hidden w-6"/>
        <motion.div
            initial={{x: "100%"}}
            animate={{x: open ? "0%" : "100%"}}
            transition={{
                type: "spring",
                stiffness: 500,
                damping: 25,
                duration: 0.1,
            }}
            className="
                fixed w-screen z-45 h-screen flex right-0
                md:w-72
                lg:sticky lg:top-0 lg:min-w-72 lg:w-72 lg:flex lg:!transform-none
                flex-col gap-4 px-4 justify-center
                bg-dark-background noise
                border-l-2 border-dark-surface
            "
        >
            <div className="flex flex-col w-full gap-2 pt-4">
                <h1 className="text-lg font-bold text-center pb-2">SETTINGS</h1>
                <ToggleElement id={`${pageDocument.name}-build-tool`} title="Build Tool" state={buildToolState}/>
                <ToggleElement id={`${pageDocument.name}-language`} title="Language" state={languageState}/>
                <ToggleElement id={`${pageDocument.name}-platform`} title="Platform" state={platformState}/>
            </div>
            <div className="[&>*]:p-1 grow">
                <Separator/>
                <h2 className="text-lg font-bold text-center !pt-2">ON THIS PAGE</h2>
                <TableOfContents
                    pageDocument={pageDocument}
                    buildToolState={buildToolState}
                    languageState={languageState}
                    platformState={platformState}
                />
            </div>
        </motion.div>
    </>
}

type TrackedElement = {
    id: string,
    target: Element,
}

function TableOfContents({pageDocument, buildToolState, languageState, platformState}: {
    pageDocument: PageDocument,
    buildToolState: ConfigurationState,
    languageState: ConfigurationState,
    platformState: ConfigurationState,
}) {

    const [buildTool] = buildToolState
    const [language] = languageState
    const [platform] = platformState

    const params = useParams()
    const [activeSection, setActiveSection] = useState<string | null>(null)

    useEffect(() => {
        const elements: TrackedElement[] = Array.from(document.querySelectorAll("#doc-section a"))
            .map(element => element.lastChild as Element)
            .filter(element => element.tagName === "H1" || element.tagName === "H2")
            .map(element => ({id: element.id, target: element}))

        let sections: Map<Element, string> = new Map()
        for (let element of elements) {
            sections.set(element.target, element.id)
        }

        let visibleElements = new Set<Element>()

        const callback = (entries: IntersectionObserverEntry[]) => {
            for (let entry of entries) {
                if (entry.isIntersecting) {
                    visibleElements.add(entry.target)
                } else {
                    visibleElements.delete(entry.target)
                }
            }

            let firstVisibleSection = Array.from(sections.entries()).find(([element]) => visibleElements.has(element))
            if (!firstVisibleSection) return
            setActiveSection(firstVisibleSection[1])
        }

        const observer = new IntersectionObserver(callback, {
            root: null,
            rootMargin: "0px",
            threshold: [1.0],
        })

        Array.from(sections.keys()).forEach((element) => observer.observe(element))

        return () => observer.disconnect()
    }, [params, language, platform, buildTool])

    return <>
        {
            pageDocument.sections.map((section) => {
                return <Section key={section.id} section={section} selected={section.id === activeSection}/>
            })
        }
    </>
}

function Section({section, selected}: { section: PageContent, selected: boolean }) {

    const color = selected ? "text-(--project-color)" : ""

    let level = ""
    switch (section.level) {
        case 2:
            level = "ml-6 text-sm"
            break
        case 3:
            level = "ml-12"
            break
    }

    return <div className={`${level} ${color} transition ease-in-out hover:text-(--project-color)`}>
        <a href={`#${section.id}`}>{section.name}</a>
    </div>
}

function ToggleElement({id, title, state}: { id: string, title: string, state: ConfigurationState }) {
    const [current, values, setStoredKey] = state

    if (values.length <= 1) return <></>

    return <>
        <ElementTitle text={title}/>
        <div className="flex gap-1">
            {
                values.map((option, index) => {

                    const isFirst = index === 0
                    const isLast = index === values.length - 1

                    const leftBorder = isFirst ? "rounded-l-lg" : ""
                    const rightBorder = isLast ? "rounded-r-lg" : ""

                    const isSelected = option.key === current.key

                    const pointer = isSelected ? "cursor-default" : "cursor-pointer"

                    return <div
                        key={`toggle-element-${index}`}
                        className={`${pointer} ${leftBorder} ${rightBorder} relative h-8 flex-1 flex justify-center items-center bg-dark-background`}
                        onClick={() => setStoredKey(option.key)}
                    >
                        {isSelected && (
                            <div
                                className={`${leftBorder} ${rightBorder} z-30 absolute w-full h-full bg-(--project-color)`}/>
                        )}
                        <span className="relative z-50">{option.name}</span>
                    </div>
                })
            }
        </div>
    </>
}

function ElementTitle({text}: { text: string }) {
    return <div className="flex justify-center items-center">{text}</div>
}
