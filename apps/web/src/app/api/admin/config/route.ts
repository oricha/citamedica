import { PrismaClient } from '@prisma/client';
import { NextResponse } from 'next/server';
import { getServerSession } from 'next-auth/next';
import { authOptions } from '../../auth/[...nextauth]/route';

const prisma = new PrismaClient();

export async function GET() {
  try {
    const session = await getServerSession(authOptions);
    if (!session || session.user.role !== 'SYSTEM_ADMIN') {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    const rows = await prisma.systemConfig.findMany();
    const config = Object.fromEntries(rows.map((r) => [r.key, r.value]));
    return NextResponse.json({ data: config });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching config' }, { status: 500 });
  }
}

export async function POST(req: Request) {
  try {
    const session = await getServerSession(authOptions);
    if (!session || session.user.role !== 'SYSTEM_ADMIN') {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    const body = await req.json();
    const entries = Object.entries(body || {}) as Array<[string, string]>;
    for (const [key, value] of entries) {
      if (!key) continue;
      await prisma.systemConfig.upsert({
        where: { key },
        create: { key, value: String(value) },
        update: { value: String(value) },
      });
    }
    await prisma.auditLog.create({
      data: { actorUserId: String(session.user.id), action: 'UPDATE_SYSTEM_CONFIG', metadata: { keys: entries.map(([k]) => k) } },
    });
    return NextResponse.json({ success: true });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error updating config' }, { status: 500 });
  }
}

