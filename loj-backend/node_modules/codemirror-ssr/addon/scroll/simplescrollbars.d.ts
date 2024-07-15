import "codemirror";

declare module "codemirror" {
    interface ScrollbarModels {
        simple: ScrollbarModelConstructor;
        overlay: ScrollbarModelConstructor;
    }
}
export default function use(cm: typeof import('codemirror')): void;