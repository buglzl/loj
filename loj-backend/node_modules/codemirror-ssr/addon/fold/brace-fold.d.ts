import './foldcode';

declare module './foldcode' {
    interface FoldHelpers {
        brace: FoldRangeFinder;
        import: FoldRangeFinder;
        include: FoldRangeFinder;
    }
}
export default function use(cm: typeof import('codemirror')): void;