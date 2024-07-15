import './show-hint';

declare module "codemirror" {
    interface HintHelpers {
        css: HintFunction;
    }
}
export default function use(cm: typeof import('codemirror')): void;