
export function Content() {
  return(
    <div className="w-screen h-screen pt-12">
      <div
        className="absolute top-0 z-2 h-screen w-screen bg-[radial-gradient(75%_75%_at_95%_0%,rgba(0,163,255,0.1)_0,rgba(0,163,255,0)_75%,rgba(0,163,255,0)_100%)]"/>
      <div
        className="absolute inset-0 z-3 h-full w-full bg-[radial-gradient(#202023_1px,transparent_1px)] [background-size:16px_16px]"/>
    </div>
  )
}