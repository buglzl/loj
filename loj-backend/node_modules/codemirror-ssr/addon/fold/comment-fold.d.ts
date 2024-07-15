import './foldcode';

declare module './foldcode' {
    interface FoldHelpers {
        comment: FoldRangeFinder;
    }
}
export default function use(cm: typeof import('codemirror')): void;