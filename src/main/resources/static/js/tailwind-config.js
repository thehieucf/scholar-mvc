/**
 * tailwind-config.js — Cấu hình Tailwind CSS cho Rhythmic Scholar
 * Tệp này định nghĩa bảng màu, font chữ và các tùy chỉnh giao diện khác.
 * Dùng chung cho tất cả các trang. Load SAU khi đã import Tailwind CDN.
 */
tailwind.config = {
    theme: {
        extend: {
            // Định nghĩa các màu sắc tùy chỉnh theo Material Design 3
            colors: {
                'surface-container-low':          '#f1f4f3',
                'on-tertiary':                    '#ffffff',
                'secondary-fixed-dim':            '#a4c9ff',
                'background':                     '#f7faf9',
                'tertiary-fixed-dim':             '#ebc23e',
                'on-primary-fixed-variant':       '#005049',
                'surface-container-lowest':       '#ffffff',
                'on-background':                  '#181c1c',
                'on-tertiary-container':          '#816700',
                'secondary-fixed':                '#d4e3ff',
                'on-error':                       '#ffffff',
                'on-primary-container':           '#00776d',
                'on-secondary-container':         '#003e73',
                'inverse-primary':                '#56dacc',
                'surface-container-high':         '#e6e9e8',
                'secondary':                      '#0060ac',
                'outline-variant':                '#bdc9c2',
                'error':                          '#ba1a1a',
                'tertiary-fixed':                 '#ffe087',
                'on-surface':                     '#181c1c',
                'inverse-on-surface':             '#eef1f0',
                'primary':                        '#006a62',
                'on-secondary':                   '#ffffff',
                'tertiary-container':             '#ffeab4',
                'error-container':                '#ffdad6',
                'on-primary-fixed':               '#00201d',
                'surface-dim':                    '#d7dbda',
                'on-surface-variant':             '#3e4944',
                'on-error-container':             '#93000a',
                'primary-fixed-dim':              '#56dacc',
                'surface':                        '#f7faf9',
                'surface-tint':                   '#006a62',
                'surface-container-highest':      '#e0e3e2',
                'on-secondary-fixed':             '#001c39',
                'on-secondary-fixed-variant':     '#004883',
                'on-tertiary-fixed-variant':      '#574500',
                'primary-container':              '#8afff0',
                'outline':                        '#6e7a74',
                'primary-fixed':                  '#76f7e8',
                'surface-bright':                 '#f7faf9',
                'surface-container':              '#ebeeed',
                'surface-variant':                '#e0e3e2',
                'inverse-surface':                '#2d3131',
                'secondary-container':            '#68abff',
                'on-tertiary-fixed':              '#241a00',
                'on-primary':                     '#ffffff',
                'tertiary':                       '#735c00',
            },
            // Tùy chỉnh bo góc cho các thành phần (nút, thẻ, container)
            borderRadius: {
                DEFAULT: '1rem',
                lg:      '2rem',
                xl:      '3rem',
                full:    '9999px',
            },
            // Cấu hình font chữ cho từng mục đích sử dụng
            fontFamily: {
                headline: ['Plus Jakarta Sans'], // Cho tiêu đề lớn
                body:     ['Be Vietnam Pro'],    // Cho nội dung chính
                label:    ['Be Vietnam Pro'],    // Cho các nhãn và mô tả nhỏ
            },
        },
    },
};
