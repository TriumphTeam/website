import {Link, useLocation, useParams} from "react-router"
import type {ConfigurationState} from "~/hooks/useConfiguration"
import useSWR from "swr"
import parse from "html-react-parser"
import {
    BOLD_COMPONENT_TYPE, BUILDTOOL_CONDITION_TYPE,
    BULLET_LIST_COMPONENT_TYPE,
    BulletListComponent,
    CODE_BLOCK_COMPONENT_TYPE,
    CODE_COMPONENT_TYPE,
    CodeBlockComponent,
    CodeComponent,
    CONDITIONAL_COMPONENT_TYPE,
    ConditionalComponent,
    type DocComponent,
    HARD_LINE_BREAK_COMPONENT_TYPE,
    HEADER_COMPONENT_TYPE,
    type HeaderComponent,
    HINT_COMPONENT_TYPE,
    HintComponent,
    HintType,
    IMAGE_COMPONENT_TYPE,
    ImageComponent,
    ITALIC_COMPONENT_TYPE, LANGUAGE_CONDITION_TYPE, LanguageCondition,
    LINK_COMPONENT_TYPE,
    LinkComponent,
    LIST_ITEM_COMPONENT_TYPE,
    ListItemComponent,
    ORDERED_LIST_COMPONENT_TYPE,
    OrderedListComponent,
    type PageDocument,
    PARAGRAPH_COMPONENT_TYPE,
    ParagraphComponent, PLATFORM_CONDITION_TYPE,
    QUOTE_COMPONENT_TYPE,
    QuoteComponent,
    ROOT_COMPONENT_TYPE,
    type RootComponent,
    SEPARATOR_COMPONENT_TYPE,
    SOFT_LINE_BREAK_COMPONENT_TYPE,
    STRIKETHROUGH_COMPONENT_TYPE,
    TEXT_COMPONENT_TYPE,
    TextComponent,
    UNDERLINE_COMPONENT_TYPE,
    type WithChildren,
} from "@lichthund/triumph-docs-serializable"
import {type ReactNode, useEffect, useState} from "react"
import "./one_dark.css"
import type {DocumentConfiguration} from "~/utils/Configurations"

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
    const location = useLocation()
    const {data, error} = useSWR<PageDocument>(`/page?version=${version}&page=${page}`)

    useEffect(() => {
        if (location.hash && data) {
            setTimeout(() => {
                const element = document.getElementById(location.hash.substring(1))
                if (element) {
                    element.scrollIntoView({behavior: "smooth"})
                }
            }, 100)
        }
    }, [location.hash, data])

    const [buildTool] = buildToolConfigurationState
    const [language] = languageConfigurationState
    const [platform] = platformConfigurationState

    if (error || !data) return <div>Failed to load</div>

    return (
        <div className="w-full h-screen pt-12">
            <div
                className="absolute top-0 z-2 h-screen w-full bg-[radial-gradient(75%_75%_at_95%_0%,rgba(0,163,255,0.1)_0,rgba(0,163,255,0)_75%,rgba(0,163,255,0)_100%)]"
            />

            <PageContent
                key="page-content"
                document={data}
                buildTool={buildTool}
                language={language}
                platform={platform}
            />
        </div>
    )
}

function PageContent({document, buildTool, language, platform}: {
    document: PageDocument,
    buildTool: DocumentConfiguration,
    language: DocumentConfiguration,
    platform: DocumentConfiguration,
}) {

    function DocComponents({component}: { component: RootComponent }) {
        return <ChildComponent components={component.children.children}/>
    }

    function ChildComponent({components}: { components: any[] }) {
        return <>
            {
                components.map(component => {
                    if (component.type === ROOT_COMPONENT_TYPE.get()) {
                        return <DocComponents component={component as RootComponent}/>
                    }

                    if (component.type === HEADER_COMPONENT_TYPE.get()) {
                        return <HeaderDocComponent component={component as HeaderComponent}/>
                    }

                    if (component.type === TEXT_COMPONENT_TYPE.get()) {
                        return <TextDocComponent component={component as TextComponent}/>
                    }

                    if (component.type === PARAGRAPH_COMPONENT_TYPE.get()) {
                        return <ParagraphDocComponent component={component as ParagraphComponent}/>
                    }

                    if (component.type === SOFT_LINE_BREAK_COMPONENT_TYPE.get()) {
                        return <> </> // a soft line break is just a space.
                    }

                    if (component.type === HARD_LINE_BREAK_COMPONENT_TYPE.get()) {
                        return <br/> // a hard line break will break the line.
                    }

                    if (component.type === QUOTE_COMPONENT_TYPE.get()) {
                        return <QuoteDocComponent component={component as QuoteComponent}/>
                    }

                    if (component.type === BULLET_LIST_COMPONENT_TYPE.get()) {
                        return <BulletListDocComponent component={component as BulletListComponent}/>
                    }

                    if (component.type === ORDERED_LIST_COMPONENT_TYPE.get()) {
                        return <OrderedListDocComponent component={component as OrderedListComponent}/>
                    }

                    if (component.type === CODE_COMPONENT_TYPE.get()) {
                        return <CodeDocComponent component={component as CodeComponent}/>
                    }

                    if (component.type === CODE_BLOCK_COMPONENT_TYPE.get()) {
                        return <CodeBlockDocComponent component={component as CodeBlockComponent}/>
                    }

                    if (component.type === BOLD_COMPONENT_TYPE.get()) {
                        return <span className="font-bold"><ChildComponent
                            components={(component as WithChildren).children.children}/></span>
                    }

                    if (component.type === ITALIC_COMPONENT_TYPE.get()) {
                        return <span className="italic">
                        <ChildComponent components={(component as WithChildren).children.children}/>
                    </span>
                    }

                    if (component.type === STRIKETHROUGH_COMPONENT_TYPE.get()) {
                        return <span className="line-through">
                        <ChildComponent components={(component as WithChildren).children.children}/>
                    </span>
                    }

                    if (component.type === UNDERLINE_COMPONENT_TYPE.get()) {
                        return <span className="underline">
                        <ChildComponent components={(component as WithChildren).children.children}/>
                    </span>
                    }

                    if (component.type === SEPARATOR_COMPONENT_TYPE.get()) {
                        return <Separator/>
                    }

                    if (component.type === LINK_COMPONENT_TYPE.get()) {
                        return <LinkDocComponent component={component as LinkComponent}/>
                    }

                    if (component.type === IMAGE_COMPONENT_TYPE.get()) {
                        return <ImageDocComponent component={component as ImageComponent}/>
                    }

                    if (component.type === HINT_COMPONENT_TYPE.get()) {
                        return <HintDocComponent component={component as HintComponent}/>
                    }

                    if (component.type === CONDITIONAL_COMPONENT_TYPE.get()) {
                        return <ConditionalDocComponent component={component as ConditionalComponent}/>
                    }

                    return <></>
                })
            }
        </>
    }

    function HeaderDocComponent({component}: { component: HeaderComponent }) {

        let size: string = "text-base"
        switch (component.level) {
            case 1:
                size = "text-2xl"
                break
            case 2:
                size = "text-xl"
                break
            case 3:
                size = "text-lg"
                break
            case 4:
                size = "text-md"
                break
            case 5:
                size = "text-base"
                break
            case 6:
                size = "text-sm"
                break
        }

        return <a id={component.id} href={`#${component.id}`}
                  className={`${size} w-2/3 group flex flex-row font-medium text-white`}>
            <span id="hash" className="absolute -ml-6 opacity-0 group-hover:opacity-20 transition-opacity">#</span>
            <h2><ChildComponent components={component.children.children}/></h2>
        </a>
    }

    function TextDocComponent({component}: { component: TextComponent }) {
        return <>{component.content}</>
    }

    function ParagraphDocComponent({component}: { component: ParagraphComponent }) {
        return <p><ChildComponent components={component.children.children}/></p>
    }

    function QuoteDocComponent({component}: { component: QuoteComponent }) {
        return <HintBlock type={undefined}>
            <ChildComponent components={component.children.children}/>
        </HintBlock>
    }

    function HintDocComponent({component}: { component: HintComponent }) {
        return <HintBlock type={component.hintType}>
            <ChildComponent components={component.children.children}/>
        </HintBlock>
    }

    function HintBlock({type, children}: { type: HintType | undefined, children: ReactNode }) {

        let color: string = "border-neutral-400"
        let icon: string | undefined = undefined

        if (type) {
            switch (type.toString()) {
                case HintType.INFO.name:
                    color = "border-(--hint-info)"
                    break
                case HintType.SUCCESS.name:
                    color = "border-(--hint-success)"
                    break
                case HintType.WARNING.name:
                    color = "border-(--hint-warning)"
                    break
                case HintType.ERROR.name:
                    color = "border-(--hint-error)"
                    break
            }

            switch (type.toString()) {
                case HintType.INFO.name:
                    icon = "fa-solid fa-circle-exclamation text-(--hint-info)"
                    break
                case HintType.SUCCESS.name:
                    icon = "fa-solid fa-circle-check text-(--hint-success)"
                    break
                case HintType.WARNING.name:
                    icon = "fa-solid fa-triangle-exclamation text-(--hint-warning)"
                    break
                case HintType.ERROR.name:
                    icon = "fa-solid fa-circle-xmark text-(--hint-error)"
                    break
            }
        }

        return <div
            className={`border-l-4 ${color} bg-dark-background-secondary !pl-4 !py-4 my-6 mx-2 rounded-l-sm rounded-r-md flex flex-row items-center`}>
            {icon && <i className={`${icon} pr-4 text-xl`}/>}
            {children}
        </div>
    }

    function BulletListDocComponent({component}: { component: BulletListComponent }) {
        return <ul className="list-disc list-outside !pl-8">
            <ListItemDocComponent components={component.children.children}/>
        </ul>
    }

    function OrderedListDocComponent({component}: { component: OrderedListComponent }) {
        return <ol className="list-decimal list-outside !pl-8">
            <ListItemDocComponent components={component.children.children}/>
        </ol>
    }

    function ListItemDocComponent({components}: { components: any[] }) {
        return <>
            {
                components.map(component => {
                    const anyComponent = component as any

                    if (anyComponent.type === LIST_ITEM_COMPONENT_TYPE.get()) {
                        const listItemComponent = anyComponent as ListItemComponent
                        return <li><ChildComponent components={listItemComponent.children.children}/></li>
                    }

                    if (anyComponent.type === BULLET_LIST_COMPONENT_TYPE.get()) {
                        const bulletListComponent = anyComponent as BulletListComponent
                        return <BulletListDocComponent component={bulletListComponent}/>
                    }

                    if (anyComponent.type === ORDERED_LIST_COMPONENT_TYPE.get()) {
                        const orderedListComponent = anyComponent as OrderedListComponent
                        return <OrderedListDocComponent component={orderedListComponent}/>
                    }

                    return <></>
                })
            }
        </>
    }

    function CodeDocComponent({component}: { component: CodeComponent }) {
        return <code>{component.content}</code>
    }

    function CodeBlockDocComponent({component}: { component: CodeBlockComponent }) {
        const [isCopied, setIsCopied] = useState(false)

        // fa-solid fa-check
        const icon = isCopied ? "fa-solid fa-check text-(--project-color) cursor-default" : "fa-solid fa-copy cursor-pointer"
        const className = `${icon} text-xl absolute right-0 -translate-x-[125%] translate-y-full opacity-0 group-hover:opacity-20 transition-opacity`

        const handleCopy = () => {
            navigator.clipboard.writeText(component.raw)
                .then(
                    r => {
                        setIsCopied(true)
                        setTimeout(() => {
                            setIsCopied(false)
                        }, 3000)
                    },
                    e => {
                        console.log(e)
                    },
                )
        }

        return <div className="relative group">
            {!isCopied ? <i className={className} onClick={handleCopy}/> : <i className={className}/>}
            <pre lang={component.lang}>
                <code lang={component.lang}>{parse(component.content)}</code>
            </pre>
        </div>
    }

    function Separator() {
        return <hr
            className="!p-0 my-4 h-px border-t-0 bg-transparent bg-gradient-to-r from-transparent  to-transparent opacity-25 via-neutral-400"
        />
    }

    function LinkDocComponent({component}: { component: LinkComponent }) {

        const className = "text-(--project-color) hover:text-(--project-color)/70 transition-colors duration-300 ease-in-out"

        if (!component.destination.startsWith("/")) {
            return <a className={className} href={component.destination} target="_blank" rel="noreferrer">
                <ChildComponent components={component.children.children}/>
            </a>
        }

        return <Link to={`../${component.destination}`} relative="path" className={className}>
            <ChildComponent components={component.children.children}/>
        </Link>
    }

    function ImageDocComponent({component}: { component: ImageComponent }) {
        return <img src={component.destination} alt={component.alt} title={component.title ?? ""}/>
    }

    function ConditionalDocComponent({component}: { component: ConditionalComponent }) {

        const condition = component.condition as any

        if (condition.type === PLATFORM_CONDITION_TYPE.get()) {
            return <></>
        }

        if (condition.type === LANGUAGE_CONDITION_TYPE.get()) {
            const languageCondition = condition as LanguageCondition

            if (languageCondition.language === language.key) {
                return <ChildComponent components={[component.value] as const}/>
            }

            return <></>
        }

        if (condition.type === BUILDTOOL_CONDITION_TYPE.get()) {
            return <></>
        }

        return <></>
    }

    return <div
        className="relative xl:ml-60 2xl:ml-72 px-12 py-8 z-5 [&>*]:p-2  bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]">
        <h1 className="text-4xl font-medium text-white text-center pointer-events-none">{document.name}</h1>
        <h2 className="text-lg text-center">{document.description}</h2>
        <Separator/>
        {/* More complex content is rendered from here on out.*/}
        <DocComponents component={document.content}/>
    </div>
}

