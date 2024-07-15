import '../..';
import './searchcursor';

export interface SearchAnnotation {
    clear(): void;
}

declare module "codemirror" {
    interface Editor {
        showMatchesOnScrollbar(query: string | RegExp, caseFold?: boolean, className?: string): SearchAnnotation;
    }
}
export default function use(cm: typeof import('codemirror')): void;