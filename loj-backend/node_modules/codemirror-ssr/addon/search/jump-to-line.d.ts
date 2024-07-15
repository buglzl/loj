import '../..';
import '../dialog/dialog';

declare module "codemirror" {
    interface CommandActions {
        jumpToLine(cm: Editor): void;
    }

    interface EditorConfiguration {
        search?: {
            bottom: boolean;
        } | undefined;
    }
}
export default function use(cm: typeof import('codemirror')): void;