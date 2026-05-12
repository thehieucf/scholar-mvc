window.tailwind = window.tailwind || {};
window.tailwind.config = {
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                "outline-variant": "#c4c5d5",
                "secondary-container": "#2170e4",
                "surface-tint": "#3755c3",
                "tertiary": "#303539",
                "surface-container-low": "#eff4ff",
                "on-primary-container": "#a8b8ff",
                "background": "#f8f9ff",
                "on-secondary-container": "#fefcff",
                "on-tertiary-fixed-variant": "#43474b",
                "surface-container-lowest": "#ffffff",
                "primary-fixed-dim": "#b8c4ff",
                "on-tertiary": "#ffffff",
                "on-primary": "#ffffff",
                "on-secondary": "#ffffff",
                "on-surface-variant": "#444653",
                "surface": "#f8f9ff",
                "on-tertiary-fixed": "#171c1f",
                "surface-container": "#e5eeff",
                "surface-variant": "#d3e4fe",
                "on-primary-fixed-variant": "#173bab",
                "secondary-fixed-dim": "#adc6ff",
                "surface-bright": "#f8f9ff",
                "on-primary-fixed": "#001453",
                "surface-dim": "#cbdbf5",
                "on-secondary-fixed-variant": "#004395",
                "inverse-on-surface": "#eaf1ff",
                "on-background": "#0b1c30",
                "surface-container-highest": "#d3e4fe",
                "error": "#ba1a1a",
                "primary-container": "#1e40af",
                "inverse-primary": "#b8c4ff",
                "on-secondary-fixed": "#001a42",
                "tertiary-fixed-dim": "#c3c7cb",
                "secondary-fixed": "#d8e2ff",
                "secondary": "#0058be",
                "outline": "#757684",
                "surface-container-high": "#dce9ff",
                "on-error": "#ffffff",
                "tertiary-container": "#474c4f",
                "error-container": "#ffdad6",
                "tertiary-fixed": "#dfe3e7",
                "on-surface": "#0b1c30",
                "on-error-container": "#93000a",
                "on-tertiary-container": "#b8bcc0",
                "primary-fixed": "#dde1ff",
                "primary": "#00288e",
                "inverse-surface": "#213145"
            },
            borderRadius: {
                DEFAULT: "0.25rem",
                lg: "0.5rem",
                xl: "0.75rem",
                full: "9999px"
            },
            spacing: {
                gutter: "1.5rem",
                "container-max-width": "1440px",
                "stack-md": "1rem",
                "stack-sm": "0.5rem",
                "margin-mobile": "1rem",
                "sidebar-width": "260px",
                "stack-lg": "1.5rem",
                "margin-desktop": "2rem"
            },
            fontFamily: {
                "body-sm": ["Noto Sans"],
                "data-table": ["Inter"],
                "display-lg": ["Noto Sans"],
                "title-sm": ["Noto Sans"],
                "body-md": ["Noto Sans"],
                "label-caps": ["Inter"],
                "headline-md": ["Noto Sans"]
            },
            fontSize: {
                "body-sm": ["14px", { lineHeight: "20px", fontWeight: "400" }],
                "data-table": ["14px", { lineHeight: "20px", fontWeight: "500" }],
                "display-lg": ["32px", { lineHeight: "40px", letterSpacing: "-0.02em", fontWeight: "700" }],
                "title-sm": ["18px", { lineHeight: "24px", fontWeight: "600" }],
                "body-md": ["16px", { lineHeight: "24px", fontWeight: "400" }],
                "label-caps": ["12px", { lineHeight: "16px", letterSpacing: "0.05em", fontWeight: "600" }],
                "headline-md": ["24px", { lineHeight: "32px", letterSpacing: "-0.01em", fontWeight: "600" }]
            }
        }
    }
};
