import "codemirror";

export interface Ruler {
    column: number;
    className?: string | undefined;
    color?: string | undefined;
    lineStyle?: string | undefined;
    width?: string | undefined;
}

declare module "codemirror" {
    interface EditorConfiguration {
        /** show one or more vertical rulers in the editor. */
         rulers?: false | ReadonlyArray<number | Ruler> | undefined;
    }
}
export default function use(cm: typeof import('codemirror')): void;