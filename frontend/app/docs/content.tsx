import {useLocation, useParams} from "react-router"
import type {ConfigurationState} from "~/hooks/useConfiguration"
import useSWR from "swr"
import {type PageDocument, type PageContent} from "@lichthund/triumph-docs-serializable"
import {useEffect} from "react"
import "./one_dark.css"
import type {DocumentConfiguration} from "~/utils/Configurations"
import {PageDocumentComponent, Separator} from "~/docs/doc-component/pageDocument"

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
        <div className="max-w-full h-screen pt-12 flex overflow-y-auto">
            <div
                className="absolute top-0 z-2 h-screen w-full bg-[radial-gradient(75%_75%_at_95%_0%,rgba(0,163,255,0.1)_0,rgba(0,163,255,0)_75%,rgba(0,163,255,0)_100%)]"
            />

            <PageContents
                key="page-content"
                document={data}
                buildTool={buildTool}
                language={language}
                platform={platform}
            />
        </div>
    )
}

function PageContents({document, buildTool, language, platform}: {
    document: PageDocument,
    buildTool: DocumentConfiguration,
    language: DocumentConfiguration,
    platform: DocumentConfiguration,
}) {


    return <div
        className="w-full flex gap-8 overflow-x-hidden relative xl:ml-60 2xl:ml-72 px-12 py-8 z-5 bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]">
        <div className="flex-1 w-full min-w-0 [&>*]:p-2">
            <h1 className="text-4xl font-medium text-white text-center pointer-events-none">{document.name}</h1>
            <h2 className="text-lg text-center">{document.description}</h2>
            <Separator/>
            {/* More complex content is rendered from here on out.*/}
            <PageDocumentComponent document={document} buildTool={buildTool} language={language} platform={platform}/>
        </div>
        <div className="max-w-72 flex-none [&>*]:p-1 sticky top-6">
            <h2 className="text-lg font-bold">ON THIS PAGE</h2>
            <TableOfContents document={document}/>
        </div>
    </div>
}

function TableOfContents({document}: { document: PageDocument }) {
    return <>
        {
            document.sections.map((section, index) => {
                return <Section section={section} selected={false}/>
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

