import "codemirror";

declare module "codemirror" {
    interface EditorConfiguration {
        /** when enabled, adds the CSS class cm-trailingspace to stretches of whitespace at the end of lines. */
         showTrailingSpace?: boolean | undefined;
    }
}
export default function use(cm: typeof import('codemirror')): void;