import * as CodeMirror from "codemirror";
import './foldcode';

export interface XmlTag {
    from: CodeMirror.Position;
    to: CodeMirror.Position;
    tag: string;
}

declare module './foldcode' {
    interface FoldHelpers {
        xml: FoldRangeFinder;
    }
}

declare module "codemirror" {
    function findMatchingTag(cm: Editor, pos: Position, range: Range): {open: XmlTag, close: XmlTag | null | undefined, at: 'open' | 'close'} | undefined;

    function findEnclosingTag(cm: Editor, pos: Position, range: Range, tag: string): {open: XmlTag, close: XmlTag} | undefined;

    function scanForClosingTag(cm: Editor, pos: Position, name: string, end?: Position): XmlTag | null | undefined;
}
export default function use(cm: typeof import('codemirror')): void;