import type { BytemdPlugin } from 'bytemd';
import { Options } from 'remark-gfm';
type Locale = {
    strike: string;
    strikeText: string;
    task: string;
    taskText: string;
    table: string;
    tableHeading: string;
};
export interface BytemdPluginGfmOptions extends Options {
    locale?: Partial<Locale>;
}
export default function gfm({ locale: _locale, ...remarkGfmOptions }?: BytemdPluginGfmOptions): BytemdPlugin;
export {};
