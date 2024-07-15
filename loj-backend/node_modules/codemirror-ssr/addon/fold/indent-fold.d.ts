import './foldcode';

declare module './foldcode' {
    interface FoldHelpers {
        indent: FoldRangeFinder;
    }
}
export default function use(cm: typeof import('codemirror')): void;