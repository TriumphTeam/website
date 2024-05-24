import Prism from "prismjs"

(function (Prism) {

  const keywords = /(^|[^.])\b(?:abstract|actual|annotation|as|break|by|catch|class|companion|const|constructor|continue|crossinline|data|do|dynamic|else|enum|expect|external|final|finally|for|fun|get|if|import|in|infix|init|inline|inner|interface|internal|is|lateinit|noinline|null|object|open|operator|out|override|package|private|protected|public|reified|return|sealed|set|super|suspend|tailrec|this|throw|to|try|typealias|val|var|vararg|when|where|while)\b/
  // full package (optional) + parent classes (optional)
  const classNamePrefix = /(^|[^\w.])(?:[a-z]\w*\s*\.\s*)*(?:[A-Z]\w*\s*\.\s*)*/.source

  const className = {
    pattern: RegExp(classNamePrefix + /[A-Z](?:[\d_A-Z]*[a-z]\w*)?\b/.source),
    lookbehind: true,
    inside: {
      "namespace": {
        pattern: /^[a-z]\w*(?:\s*\.\s*[a-z]\w*)*(?:\s*\.)?/,
        inside: {
          "punctuation": /\./,
        },
      },
      "punctuation": /\./,
    },
  }

  Prism.languages.kotlin = Prism.languages.extend("clike", {
    "keyword": {
      // The lookbehind prevents wrong highlighting of e.g. kotlin.properties.get
      pattern: keywords,
      lookbehind: true,
    },
    "function": [
      {
        pattern: /(?:`[^\r\n`]+`|\b\w+)(?=\s*\()/,
        greedy: true,
      },
      {
        pattern: /(\.)(?:`[^\r\n`]+`|\w+)(?=\s*{)/,
        lookbehind: true,
        greedy: true,
      },
      {
        pattern: /(?:`[^\r\n`]+`|\w+)(?=\s*{)/,
        greedy: true,
      },
      {
        pattern: /(?:`[^\r\n`]+`|\w+)(?=\s*<\w+>\s*{)/,
        greedy: true,
      },
      {
        pattern: /(?:`[^\r\n`]+`|\w+)(?=\s*<[\w.]+>\s*)/,
        greedy: true,
      },
    ],
    "label": [
      {
        ///(?<=\w)(@\w+)/,
        pattern: /\b(@\w+)/,
        greedy: true,
      },
    ],
    "object": [
      {
        pattern: /(([A-Z][a-z0-9]+)+)/,
        greedy: false,
      },
    ],
    "infix": [
      {
        // (?<=\w\s)(\w+)(?=\s\w)
        pattern: /\b\s(\w+)(?=\s\w)/,
        greedy: false,
      },
    ],
    "number": /\b(?:0[xX][\da-fA-F]+(?:_[\da-fA-F]+)*|0[bB][01]+(?:_[01]+)*|\d+(?:_\d+)*(?:\.\d+(?:_\d+)*)?(?:[eE][+-]?\d+(?:_\d+)*)?[fFL]?)\b/,
    "operator": /\+[+=]?|-[-=>]?|==?=?|!(?:!|==?)?|[/*%<>]=?|[?:]:?|\.\.|&&|\|\||\b(?:and|inv|or|shl|shr|ushr|xor)\b/,
  })

  //delete Prism.languages.kotlin['class-name'];

  Prism.languages.insertBefore("kotlin", "function", {
    "generics": {
      pattern: /<(?:[\w\s,.?]|&(?!&)|<(?:[\w\s,.?]|&(?!&)|<(?:[\w\s,.?]|&(?!&)|<(?:[\w\s,.?]|&(?!&))*>)*>)*>)*>/,
      inside: {
        "class-name": className,
        "keyword": keywords,
        "punctuation": /[<>(),.:]/,
        "operator": /[?&|]/,
      },
    },
  })

  Prism.languages.insertBefore("kotlin", "string", {
    "raw-string": {
      pattern: /("""|''')[\s\S]*?\1/,
      alias: "string",
      // See interpolation below
    },
  })
  Prism.languages.insertBefore("kotlin", "keyword", {
    "annotation": {
      pattern: /\B@(?:\w+:)?(?:[A-Z]\w*|\[[^\]]+])/,
      alias: "builtin",
    },
  })
  Prism.languages.insertBefore("kotlin", "function", {
    "label": {
      pattern: /\b\w+@|@\w+\b/,
      alias: "symbol",
    },
  })

  const interpolation = [
    {
      pattern: /\${[^}]+}/,
      inside: {
        "delimiter": {
          pattern: /^\${|}$/,
          alias: "variable",
        },
        rest: Prism.languages.kotlin,
      },
    },
    {
      pattern: /\$\w+/,
      alias: "variable",
    },
  ]

  Prism.languages.kotlin["string"].inside = Prism.languages.kotlin["raw-string"].inside = {
    interpolation: interpolation,
  }

  Prism.languages.kt = Prism.languages.kotlin
  Prism.languages.kts = Prism.languages.kotlin
}(Prism))
