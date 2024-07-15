import './xml-hint';

declare module "codemirror" {
    interface HintHelpers {
        html: HintFunction;
    }

    const htmlSchema: any;
}
export default function use(cm: typeof import('codemirror')): void;