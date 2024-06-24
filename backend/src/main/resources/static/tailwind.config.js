tailwind.config = {
    content: ["*.html"],
    theme: {
        extend: {
            colors: {
                primary: '#9B55BA',
                'primary-light': '#BA6EDC',
                'card-bg': '#181818',
                'card-bg-secondary': '#151515',
                'docs-bg': '#141417',
                'search-bg': '#202023',
            },
            backgroundImage: {
                'blur-effect': `url('/static/images/blur_effect.png')`,
                'dots': `url('/static/images/dots.png')`,
            }
        }
    }
}
