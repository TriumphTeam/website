"use client"

import {
    BOLD_COMPONENT_TYPE,
    BUILDTOOL_CONDITION_TYPE,
    BuildToolCondition,
    BULLET_LIST_COMPONENT_TYPE,
    BulletListComponent,
    CODE_BLOCK_COMPONENT_TYPE,
    CODE_COMPONENT_TYPE,
    CodeBlockComponent,
    CodeComponent,
    CONDITIONAL_COMPONENT_TYPE,
    ConditionalComponent,
    HARD_LINE_BREAK_COMPONENT_TYPE,
    HEADER_COMPONENT_TYPE,
    type HeaderComponent,
    HINT_COMPONENT_TYPE,
    HintComponent,
    HintType,
    IMAGE_COMPONENT_TYPE,
    ImageComponent,
    ITALIC_COMPONENT_TYPE,
    LANGUAGE_CONDITION_TYPE,
    LanguageCondition,
    LINK_COMPONENT_TYPE,
    LinkComponent,
    LIST_ITEM_COMPONENT_TYPE,
    ListItemComponent,
    ORDERED_LIST_COMPONENT_TYPE,
    OrderedListComponent,
    type PageDocument,
    PARAGRAPH_COMPONENT_TYPE,
    ParagraphComponent,
    PLATFORM_CONDITION_TYPE,
    PlatformCondition,
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
import {type ReactNode, useState} from "react"
import parse from "html-react-parser"
import {Link} from "react-router"
import type {ConfigurationState} from "~/hooks/useConfiguration"

export function PageDocumentComponent({document, buildToolState, languageState, platformState}: {
    document: PageDocument,
    buildToolState: ConfigurationState,
    languageState: ConfigurationState,
    platformState: ConfigurationState,
}) {

    const [buildTool] = buildToolState
    const [language] = languageState
    const [platform] = platformState

    function DocComponents({component}: { component: RootComponent }) {
        return <ChildComponent childKey="root" components={component.children.children}/>
    }

    function ChildComponent({childKey, components}: { childKey: string, components: any[] }) {
        return <>
            {
                components.map((component, index) => {
                    const componentKey = `${childKey}-${index}`
                    if (component.type === ROOT_COMPONENT_TYPE.get()) {
                        return <DocComponents key={componentKey} component={component as RootComponent}/>
                    }

                    if (component.type === HEADER_COMPONENT_TYPE.get()) {
                        return <HeaderDocComponent key={componentKey} component={component as HeaderComponent}/>
                    }

                    if (component.type === TEXT_COMPONENT_TYPE.get()) {
                        return <TextDocComponent key={componentKey} component={component as TextComponent}/>
                    }

                    if (component.type === PARAGRAPH_COMPONENT_TYPE.get()) {
                        return <ParagraphDocComponent key={componentKey} component={component as ParagraphComponent}/>
                    }

                    if (component.type === SOFT_LINE_BREAK_COMPONENT_TYPE.get()) {
                        return <> </> // a soft line break is just a space.
                    }

                    if (component.type === HARD_LINE_BREAK_COMPONENT_TYPE.get()) {
                        return <br key={componentKey}/> // a hard line break will break the line.
                    }

                    if (component.type === QUOTE_COMPONENT_TYPE.get()) {
                        return <QuoteDocComponent key={componentKey} component={component as QuoteComponent}/>
                    }

                    if (component.type === BULLET_LIST_COMPONENT_TYPE.get()) {
                        return <BulletListDocComponent key={componentKey} component={component as BulletListComponent}/>
                    }

                    if (component.type === ORDERED_LIST_COMPONENT_TYPE.get()) {
                        return <OrderedListDocComponent key={componentKey} component={component as OrderedListComponent}/>
                    }

                    if (component.type === CODE_COMPONENT_TYPE.get()) {
                        return <CodeDocComponent key={componentKey} component={component as CodeComponent}/>
                    }

                    if (component.type === CODE_BLOCK_COMPONENT_TYPE.get()) {
                        return <CodeBlockDocComponent key={componentKey} component={component as CodeBlockComponent}/>
                    }

                    if (component.type === BOLD_COMPONENT_TYPE.get()) {
                        return <span key={componentKey} className="font-bold">
                            <ChildComponent childKey={`bold-${componentKey}`} components={(component as WithChildren).children.children}/>
                        </span>
                    }

                    if (component.type === ITALIC_COMPONENT_TYPE.get()) {
                        return <span key={componentKey} className="italic">
                        <ChildComponent childKey={`italic-${componentKey}`} components={(component as WithChildren).children.children}/>
                    </span>
                    }

                    if (component.type === STRIKETHROUGH_COMPONENT_TYPE.get()) {
                        return <span key={componentKey} className="line-through">
                        <ChildComponent childKey={`strike-${componentKey}`} components={(component as WithChildren).children.children}/>
                    </span>
                    }

                    if (component.type === UNDERLINE_COMPONENT_TYPE.get()) {
                        return <span key={componentKey} className="underline">
                        <ChildComponent childKey={`underline-${componentKey}`} components={(component as WithChildren).children.children}/>
                    </span>
                    }

                    if (component.type === SEPARATOR_COMPONENT_TYPE.get()) {
                        return <Separator key={componentKey}/>
                    }

                    if (component.type === LINK_COMPONENT_TYPE.get()) {
                        return <LinkDocComponent key={componentKey} component={component as LinkComponent}/>
                    }

                    if (component.type === IMAGE_COMPONENT_TYPE.get()) {
                        return <ImageDocComponent key={componentKey} component={component as ImageComponent}/>
                    }

                    if (component.type === HINT_COMPONENT_TYPE.get()) {
                        return <HintDocComponent key={componentKey} component={component as HintComponent}/>
                    }

                    if (component.type === CONDITIONAL_COMPONENT_TYPE.get()) {
                        return <ConditionalDocComponent key={componentKey} component={component as ConditionalComponent}/>
                    }

                    return <></>
                })
            }
        </>
    }

    function HeaderDocComponent({component}: { component: HeaderComponent }) {

        let textSize: string = "text-base"
        switch (component.level) {
            case 1:
                textSize = "text-2xl"
                break
            case 2:
                textSize = "text-xl"
                break
            case 3:
                textSize = "text-lg"
                break
            case 4:
                textSize = "text-md"
                break
            case 5:
                textSize = "text-base"
                break
            case 6:
                textSize = "text-sm"
                break
        }

        const Header = ({id, level, textSize, children}: {
            id: string,
            level: number,
            textSize: string,
            children: ReactNode
        }) => {
            if (level === 1) return <h1 id={id} className={textSize}>{children}</h1>
            if (level === 2) return <h2 id={id} className={textSize}>{children}</h2>
            if (level === 3) return <h3 id={id} className={textSize}>{children}</h3>
            if (level === 4) return <h4 id={id} className={textSize}>{children}</h4>
            if (level === 5) return <h5 id={id} className={textSize}>{children}</h5>
            return <h6 id={id} className="text-sm">{children}</h6>
        }

        return <div id="doc-section" key={component.id} className="group">
            <a
                href={`#${component.id}`}
                className={`${textSize} inline-flex font-medium text-white`}
            >
                <span id="hash" className="absolute -ml-6 opacity-0 group-hover:opacity-20 transition-opacity">#</span>
                <Header id={component.id} level={component.level} textSize={textSize}>
                    <ChildComponent childKey={`header-${component.id}`} components={component.children.children}/>
                </Header>
            </a>
        </div>
    }

    function TextDocComponent({component}: { component: TextComponent }) {
        return <>{component.content}</>
    }

    function ParagraphDocComponent({component}: { component: ParagraphComponent }) {
        return <div><ChildComponent childKey="paragraph" components={component.children.children}/></div>
    }

    function QuoteDocComponent({component}: { component: QuoteComponent }) {
        return <HintBlock type={undefined}>
            <ChildComponent childKey="quote" components={component.children.children}/>
        </HintBlock>
    }

    function HintDocComponent({component}: { component: HintComponent }) {
        return <HintBlock type={component.hintType}>
            <ChildComponent childKey="hint" components={component.children.children}/>
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
            className={`border-l-4 ${color} bg-dark-background-secondary-transparent !pl-4 !py-4 my-6 mx-2 rounded-l-sm rounded-r-md flex flex-row items-center`}>
            {icon && <i className={`${icon} pr-4 text-xl`}/>}
            {children}
        </div>
    }

    function BulletListDocComponent({component}: { component: BulletListComponent }) {
        return <ul className="list-disc list-outside !pl-8">
            <ListItemDocComponent key="list-component" components={component.children.children}/>
        </ul>
    }

    function OrderedListDocComponent({component}: { component: OrderedListComponent }) {
        return <ol className="list-decimal list-outside !pl-8">
            <ListItemDocComponent key="list-component" components={component.children.children}/>
        </ol>
    }

    function ListItemDocComponent({components}: { components: any[] }) {
        return <>
            {
                components.map((component, index) => {
                    const anyComponent = component as any

                    if (anyComponent.type === LIST_ITEM_COMPONENT_TYPE.get()) {
                        const listItemComponent = anyComponent as ListItemComponent
                        return <li key={`list-${index}`}>
                            <ChildComponent childKey={`list-${index}`} components={listItemComponent.children.children}/>
                        </li>
                    }

                    if (anyComponent.type === BULLET_LIST_COMPONENT_TYPE.get()) {
                        const bulletListComponent = anyComponent as BulletListComponent
                        return <BulletListDocComponent
                            key={`bullet-list-component-${index}`}
                            component={bulletListComponent}
                        />
                    }

                    if (anyComponent.type === ORDERED_LIST_COMPONENT_TYPE.get()) {
                        const orderedListComponent = anyComponent as OrderedListComponent
                        return <OrderedListDocComponent
                            key={`ordered-list-component-${index}`}
                            component={orderedListComponent}
                        />
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
                    e => {},
                )
        }

        return <div className="relative group">
            {!isCopied ? <i className={className} onClick={handleCopy}/> : <i className={className}/>}
            <pre lang={component.lang}>
                <code lang={component.lang}>{parse(component.content)}</code>
            </pre>
        </div>
    }

    function LinkDocComponent({component}: { component: LinkComponent }) {

        const className = "text-(--project-color) hover:text-(--project-color)/70 transition-colors duration-300 ease-in-out"

        if (!component.destination.startsWith("/")) {
            return <a className={className} href={component.destination} target="_blank" rel="noreferrer">
                <ChildComponent childKey="link" components={component.children.children}/>
            </a>
        }

        return <Link to={`../${component.destination}`} relative="path" className={className}>
            <ChildComponent childKey="link" components={component.children.children}/>
        </Link>
    }

    function ImageDocComponent({component}: { component: ImageComponent }) {
        return <img src={component.destination} alt={component.alt} title={component.title ?? ""}/>
    }

    function ConditionalDocComponent({component}: { component: ConditionalComponent }) {

        const condition = component.condition as any

        if (condition.type === PLATFORM_CONDITION_TYPE.get()) {
            const platformCondition = condition as PlatformCondition

            if (platformCondition.platform === platform.key) {
                return <ChildComponent childKey="conditional" components={[component.value] as const}/>
            }
            return <></>
        }

        if (condition.type === LANGUAGE_CONDITION_TYPE.get()) {
            const languageCondition = condition as LanguageCondition

            if (languageCondition.language === language.key) {
                return <ChildComponent childKey="conditional" components={[component.value] as const}/>
            }
            return <></>
        }

        if (condition.type === BUILDTOOL_CONDITION_TYPE.get()) {
            const buildToolCondition = condition as BuildToolCondition

            if (buildToolCondition.buildTool === buildTool.key) {
                return <ChildComponent childKey="conditional" components={[component.value] as const}/>
            }
            return <></>
        }
    }

    return <DocComponents component={document.content}/>
}

export function Separator() {
    return <hr
        className="!p-0 my-4 h-px border-t-0 bg-transparent bg-gradient-to-r from-transparent  to-transparent opacity-25 via-neutral-400"
    />
}
