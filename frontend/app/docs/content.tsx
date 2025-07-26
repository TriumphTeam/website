import {useParams} from "react-router"
import type {ConfigurationState} from "~/hooks/useConfiguration"
import useSWR from "swr"
import {type PageContent, type PageDocument} from "@lichthund/triumph-docs-serializable"
import "./one_dark.css"
import type {DocumentConfiguration} from "~/utils/Configurations"
import {PageDocumentComponent, Separator} from "~/docs/doc-component/pageDocument"
import {useEffect, useRef, useState} from "react"

export function Content(
    {
        version,
        buildToolConfigurationState,
        languageConfigurationState,
        platformConfigurationState,
    }: {
        version: number,
        buildToolConfigurationState: ConfigurationState,
        languageConfigurationState: ConfigurationState,
        platformConfigurationState: ConfigurationState,
    },
) {

    const {page} = useParams()
    const {data, error} = useSWR<PageDocument>(`/page?version=${version}&page=${page}`)

    const [buildTool] = buildToolConfigurationState
    const [language] = languageConfigurationState
    const [platform] = platformConfigurationState

    if (error || !data) return <div>Failed to load</div>

    return (
        <div className="max-w-full h-screen flex overflow-y-auto">
            <PageContents
                key="page-content"
                pageDocument={data}
                buildTool={buildTool}
                language={language}
                platform={platform}
            />
        </div>
    )
}

function PageContents({pageDocument, buildTool, language, platform}: {
    pageDocument: PageDocument,
    buildTool: DocumentConfiguration,
    language: DocumentConfiguration,
    platform: DocumentConfiguration,
}) {

    return <div
        className="w-full flex gap-8 overflow-x-hidden relative xl:ml-60 2xl:ml-72 px-12 py-12 z-5 bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]">
        <div id="doc-content" className="flex-1 w-full min-w-0 [&>*]:p-2 pt-12">
            <h1 className="text-4xl font-medium text-white text-center pointer-events-none">{pageDocument.name}</h1>
            <h2 className="text-lg text-center">{pageDocument.description}</h2>
            <Separator/>
            {/* More complex content is rendered from here on out.*/}
            <PageDocumentComponent document={pageDocument} buildTool={buildTool} language={language}
                                   platform={platform}/>
        </div>
        <div className="max-w-72 flex-none [&>*]:p-1 sticky top-0">
            <h2 className="text-lg font-bold mt-6">ON THIS PAGE</h2>
            <TableOfContents pageDocument={pageDocument} buildTool={buildTool} language={language} platform={platform}/>
        </div>
    </div>
}

type TrackedElement = {
    id: string,
    target: Element,
}

function TableOfContents({pageDocument, buildTool, language, platform}: {
    pageDocument: PageDocument,
    buildTool: DocumentConfiguration,
    language: DocumentConfiguration,
    platform: DocumentConfiguration,
}) {

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
                return <Section section={section} selected={section.id === activeSection}/>
            })
        }
    </>
}

function Section({section, selected}: { section: PageContent, selected: boolean }) {

    const color = selected ? "text-(--project-color)" : ""

    let level = ""
    switch (section.level) {
        case 2:
            level = "ml-6"
            break
        case 3:
            level = "ml-12"
            break
    }

    return <div className={`${level} ${color} transition ease-in-out hover:text-(--project-color)`}>
        <a href={`#${section.id}`}>{section.name}</a>
    </div>
}

