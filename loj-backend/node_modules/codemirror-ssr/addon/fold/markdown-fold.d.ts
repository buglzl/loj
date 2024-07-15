import './foldcode';

declare module './foldcode' {
    interface FoldHelpers {
        markdown: FoldRangeFinder;
    }
}
export default function use(cm: typeof import('codemirror')): void;